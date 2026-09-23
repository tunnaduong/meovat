import Foundation
import Observation

/// Single source of truth for the UI.
///
/// Offline-first: bundled seed content and the last persisted user state render immediately,
/// then `refresh()` replaces them with the server's copy. Mutations apply locally right away
/// and are pushed to the API in the background; if the server can't be reached the change
/// stays on the device until the next successful refresh (server state wins).
@MainActor
@Observable
final class AppStore {
    private(set) var categories: [TipCategory]
    private(set) var tips: [Tip]

    private(set) var lists: [SavedList] { didSet { persist() } }
    var settings: AppSettings {
        didSet {
            persist()
            guard !isApplyingRemote else { return }
            let settings = settings
            sync { try await self.api.updateSettings(settings) }
        }
    }
    private(set) var checkedPrep: [String: Set<Int>] { didSet { persist() } }

    private(set) var isSyncing = false
    private(set) var syncError: String?
    private(set) var lastSyncedAt: Date?

    var l10n: L10n { L10n(language: settings.language) }

    private let api: APIClient
    private var isApplyingRemote = false

    private struct PersistedState: Codable {
        var lists: [SavedList]
        var settings: AppSettings
        var checkedPrep: [String: [Int]]
    }

    private struct ContentCache: Codable {
        var categories: [TipCategory]
        var tips: [Tip]
    }

    private static let directory: URL = {
        let dir = FileManager.default.urls(for: .applicationSupportDirectory, in: .userDomainMask)[0]
        try? FileManager.default.createDirectory(at: dir, withIntermediateDirectories: true)
        return dir
    }()
    private static let stateURL = directory.appendingPathComponent("state.json")
    private static let contentURL = directory.appendingPathComponent("content.json")

    init(api: APIClient = APIClient()) {
        self.api = api
        let url = Bundle.main.url(forResource: "seed", withExtension: "json")!
        let seed = try! JSONDecoder().decode(SeedData.self, from: Data(contentsOf: url))

        if let data = try? Data(contentsOf: Self.contentURL),
           let cache = try? JSONDecoder().decode(ContentCache.self, from: data), !cache.tips.isEmpty {
            categories = cache.categories
            tips = cache.tips
        } else {
            categories = seed.categories
            tips = seed.tips
        }

        if let data = try? Data(contentsOf: Self.stateURL),
           let state = try? JSONDecoder().decode(PersistedState.self, from: data) {
            lists = state.lists
            settings = state.settings
            checkedPrep = state.checkedPrep.mapValues(Set.init)
        } else {
            // Stagger creation dates so "newest first" keeps the seed order.
            let now = Date().timeIntervalSince1970 * 1000
            let count = seed.lists.count
            lists = seed.lists.enumerated().map { index, list in
                var list = list
                list.createdAt = now - Double(count - index) * 60_000
                return list
            }
            settings = AppSettings()
            checkedPrep = [:]
        }

        Task { await refresh() }
    }

    // MARK: - Sync

    /// Pulls content and this device's state from the API.
    func refresh() async {
        guard !isSyncing else { return }
        isSyncing = true
        defer { isSyncing = false }
        do {
            async let categories = api.categories()
            async let tips = api.tips()
            async let me = api.me()
            let (remoteCategories, remoteTips, remoteMe) = try await (categories, tips, me)
            applyRemote {
                self.categories = remoteCategories
                self.tips = remoteTips
                self.lists = remoteMe.lists
                self.settings = remoteMe.settings
                self.checkedPrep = remoteMe.checklist.mapValues(Set.init)
            }
            if let data = try? JSONEncoder().encode(ContentCache(categories: remoteCategories, tips: remoteTips)) {
                try? data.write(to: Self.contentURL, options: .atomic)
            }
            syncError = nil
            lastSyncedAt = .now
        } catch {
            syncError = error.localizedDescription
        }
    }

    private func applyRemote(_ changes: () -> Void) {
        isApplyingRemote = true
        changes()
        isApplyingRemote = false
    }

    /// Runs a background push; failures are surfaced through `syncError`, local state is kept.
    private func sync(_ operation: @escaping () async throws -> Void) {
        Task {
            do {
                try await operation()
                syncError = nil
            } catch {
                syncError = error.localizedDescription
            }
        }
    }

    private func persist() {
        let state = PersistedState(lists: lists, settings: settings, checkedPrep: checkedPrep.mapValues(Array.init))
        guard let data = try? JSONEncoder().encode(state) else { return }
        try? data.write(to: Self.stateURL, options: .atomic)
    }

    // MARK: - Content queries

    func category(id: String) -> TipCategory? { categories.first { $0.id == id } }
    func tip(id: String) -> Tip? { tips.first { $0.id == id } }
    func list(id: String) -> SavedList? { lists.first { $0.id == id } }

    func tips(in category: TipCategory) -> [Tip] { tips.filter { $0.categoryId == category.id } }

    func tags(in category: TipCategory) -> [String] {
        var seen = Set<String>()
        return tips(in: category).map(\.tag).filter { seen.insert($0).inserted }
    }

    /// Tips of a list, most recently saved first.
    func tips(in list: SavedList) -> [Tip] { list.tipIds.reversed().compactMap(tip(id:)) }

    func applySort(_ tips: [Tip]) -> [Tip] {
        switch settings.sortOrder {
        case .newest: tips
        case .alphabetical: tips.sorted { $0.title.localizedCaseInsensitiveCompare($1.title) == .orderedAscending }
        }
    }

    // MARK: - Saved lists

    func listIds(containing tipId: String) -> Set<String> {
        Set(lists.filter { $0.tipIds.contains(tipId) }.map(\.id))
    }

    func setMembership(of tipId: String, lists ids: Set<String>) {
        for index in lists.indices {
            let contains = lists[index].tipIds.contains(tipId)
            if ids.contains(lists[index].id), !contains {
                lists[index].tipIds.append(tipId)
            } else if !ids.contains(lists[index].id), contains {
                lists[index].tipIds.removeAll { $0 == tipId }
            }
        }
        sync {
            let remote = try await self.api.setMembership(tipId: tipId, listIds: ids)
            self.applyRemote { self.lists = remote }
        }
    }

    func remove(tipId: String, fromList listId: String) {
        guard let index = lists.firstIndex(where: { $0.id == listId }) else { return }
        lists[index].tipIds.removeAll { $0 == tipId }
        sync { _ = try await self.api.removeTip(listId: listId, tipId: tipId) }
    }

    @discardableResult
    func createList(name: String, description: String, emoji: String?) -> SavedList {
        let list = SavedList(emoji: emoji, name: name, description: description)
        lists.append(list)
        sync { _ = try await self.api.createList(list) }
        return list
    }

    func updateList(_ list: SavedList) {
        guard let index = lists.firstIndex(where: { $0.id == list.id }) else { return }
        lists[index] = list
        sync { _ = try await self.api.updateList(list) }
    }

    func deleteList(id: String) {
        lists.removeAll { $0.id == id }
        sync { try await self.api.deleteList(id: id) }
    }

    // MARK: - Preparation checklist

    func isPrepChecked(tipId: String, index: Int) -> Bool { checkedPrep[tipId]?.contains(index) ?? false }

    func togglePrep(tipId: String, index: Int) {
        var set = checkedPrep[tipId] ?? []
        if !set.insert(index).inserted { set.remove(index) }
        checkedPrep[tipId] = set
        let checked = set
        sync { try await self.api.setChecklist(tipId: tipId, checked: checked) }
    }
}
