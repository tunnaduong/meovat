import Foundation

struct TipCategory: Codable, Identifiable, Hashable {
    let id: String
    let emoji: String
    let name: String
    let description: String
}

struct TipStep: Codable, Hashable {
    let title: String
    let body: String
    let image: String?
}

struct Tip: Codable, Identifiable, Hashable {
    let id: String
    let categoryId: String
    let tag: String
    let title: String
    let subtitle: String
    let minutes: Int
    let image: String
    let hero: String
    let prep: [String]
    let steps: [TipStep]
}

struct SavedList: Codable, Identifiable, Hashable {
    var id: String
    var emoji: String?
    var name: String
    var description: String
    var tipIds: [String]
    var createdAt: Date

    init(id: String = UUID().uuidString, emoji: String?, name: String, description: String,
         tipIds: [String] = [], createdAt: Date = .now) {
        self.id = id
        self.emoji = emoji
        self.name = name
        self.description = description
        self.tipIds = tipIds
        self.createdAt = createdAt
    }

    private enum CodingKeys: String, CodingKey { case id, emoji, name, description, tipIds, createdAt }

    init(from decoder: Decoder) throws {
        let c = try decoder.container(keyedBy: CodingKeys.self)
        id = try c.decode(String.self, forKey: .id)
        emoji = try c.decodeIfPresent(String.self, forKey: .emoji)
        name = try c.decode(String.self, forKey: .name)
        description = try c.decodeIfPresent(String.self, forKey: .description) ?? ""
        tipIds = try c.decodeIfPresent([String].self, forKey: .tipIds) ?? []
        createdAt = try c.decodeIfPresent(Date.self, forKey: .createdAt) ?? .now
    }
}

enum AppLanguage: String, Codable, CaseIterable, Identifiable {
    case vi, en, zh, ja

    var id: String { rawValue }

    var flag: String {
        switch self {
        case .vi: "🇻🇳"
        case .en: "🇬🇧"
        case .zh: "🇨🇳"
        case .ja: "🇯🇵"
        }
    }
}

enum SortOrder: String, Codable, CaseIterable, Identifiable {
    case newest, alphabetical
    var id: String { rawValue }
}

struct AppSettings: Codable {
    var notificationsEnabled = true
    var language: AppLanguage = .vi
    var sortOrder: SortOrder = .newest

    init() {}

    private enum CodingKeys: String, CodingKey { case notificationsEnabled, language, sortOrder }

    init(from decoder: Decoder) throws {
        let c = try decoder.container(keyedBy: CodingKeys.self)
        notificationsEnabled = try c.decodeIfPresent(Bool.self, forKey: .notificationsEnabled) ?? true
        language = try c.decodeIfPresent(AppLanguage.self, forKey: .language) ?? .vi
        sortOrder = try c.decodeIfPresent(SortOrder.self, forKey: .sortOrder) ?? .newest
    }
}

struct SeedData: Codable {
    let categories: [TipCategory]
    let lists: [SavedList]
    let tips: [Tip]
}
