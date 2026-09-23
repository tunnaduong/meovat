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
    /// Absolute URL from the API; nil for bundled seed content (falls back to the `image` asset).
    let imageUrl: String?
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
    let imageUrl: String?
    let heroUrl: String?
}

struct SavedList: Codable, Identifiable, Hashable {
    var id: String
    var emoji: String?
    var name: String
    var description: String
    var tipIds: [String]
    /// Milliseconds since 1970, matching the API.
    var createdAt: Double

    init(id: String = UUID().uuidString.lowercased(), emoji: String?, name: String, description: String,
         tipIds: [String] = [], createdAt: Double = Date().timeIntervalSince1970 * 1000) {
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
        createdAt = try c.decodeIfPresent(Double.self, forKey: .createdAt) ?? Date().timeIntervalSince1970 * 1000
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

/// `GET /api/me`: everything a device needs on launch.
struct MeResponse: Codable {
    let deviceId: String
    let lists: [SavedList]
    let settings: AppSettings
    let checklist: [String: [Int]]
}

struct SeedData: Codable {
    let categories: [TipCategory]
    let lists: [SavedList]
    let tips: [Tip]
}
