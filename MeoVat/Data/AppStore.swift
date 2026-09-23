import Foundation
import Observation

/// Single source of truth: bundled seed content + user state persisted as JSON in Application Support.
@MainActor
@Observable
final class AppStore {
    let categories: [TipCategory]
    let tips: [Tip]

    private(set) var lists: [SavedList] { didSet { persist() } }
    var settings: AppSettings { didSet { persist() } }
    private(set) var checkedPrep: [String: Set<Int>] { didSet { persist() } }

    var l10n: L10n { L10n(language: settings.language) }

    private struct PersistedState: Codable {
        var lists: [SavedList]
        var settings: AppSettings
        var checkedPrep: [String: [Int]]
    }

    private static let stateURL: URL = {
        let dir = FileManager.default.urls(for: .applicationSupportDirectory, in: .userDomainMask)[0]
        try? FileManager.default.createDirectory(at: dir, withIntermediateDirectories: true)
        return dir.appendingPathComponent("state.json")
    }()

    init() {
        let url = Bundle.main.url(forResource: "seed", withExtension: "json")!
        let seed = try! JSONDecoder().decode(SeedData.self, from: Data(contentsOf: url))
        categories = seed.categories
        tips = seed.tips

        if let data = try? Data(contentsOf: Self.stateURL),
           let state = try? JSONDecoder().decode(PersistedState.self, from: data) {
            lists = state.lists
            settings = state.settings
            checkedPrep = state.checkedPrep.mapValues(Set.init)
        } else {
            // Stagger creation dates so "newest first" keeps the seed order.
            let count = seed.lists.count
            lists = seed.lists.enumerated().map { index, list in
                var list = list
                list.createdAt = Date(timeIntervalSinceNow: Double(index - count) * 60)
                return list
            }
            settings = AppSettings()
            checkedPrep = [:]
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
    }

    func remove(tipId: String, fromList listId: String) {
        guard let index = lists.firstIndex(where: { $0.id == listId }) else { return }
        lists[index].tipIds.removeAll { $0 == tipId }
    }

    @discardableResult
    func createList(name: String, description: String, emoji: String?) -> SavedList {
        let list = SavedList(emoji: emoji, name: name, description: description)
        lists.append(list)
        return list
    }

    func updateList(_ list: SavedList) {
        guard let index = lists.firstIndex(where: { $0.id == list.id }) else { return }
        lists[index] = list
    }

    func deleteList(id: String) { lists.removeAll { $0.id == id } }

    // MARK: - Preparation checklist

    func isPrepChecked(tipId: String, index: Int) -> Bool { checkedPrep[tipId]?.contains(index) ?? false }

    func togglePrep(tipId: String, index: Int) {
        var set = checkedPrep[tipId] ?? []
        if !set.insert(index).inserted { set.remove(index) }
        checkedPrep[tipId] = set
    }
}
