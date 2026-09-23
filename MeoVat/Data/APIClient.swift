import Foundation

struct APIError: LocalizedError {
    let status: Int
    let message: String
    var errorDescription: String? { message }
}

/// Thin async client for the PHP backend. Every user-state call carries the device id.
final class APIClient: Sendable {
    let baseURL: URL
    let deviceId: String
    private let session: URLSession
    private let decoder = JSONDecoder()
    private let encoder = JSONEncoder()

    init(baseURL: URL = AppConfig.apiBaseURL, deviceId: String = DeviceIdentity.id) {
        self.baseURL = baseURL
        self.deviceId = deviceId
        let configuration = URLSessionConfiguration.default
        configuration.timeoutIntervalForRequest = 15
        configuration.waitsForConnectivity = false
        session = URLSession(configuration: configuration)
    }

    // MARK: - Content

    func categories() async throws -> [TipCategory] { try await get("/api/categories") }
    func tips() async throws -> [Tip] { try await get("/api/tips") }

    // MARK: - User state

    func me() async throws -> MeResponse { try await get("/api/me") }

    func createList(_ list: SavedList) async throws -> SavedList {
        try await send("POST", "/api/lists", body: ListPayload(id: list.id, name: list.name, description: list.description, emoji: list.emoji))
    }

    func updateList(_ list: SavedList) async throws -> SavedList {
        try await send("PATCH", "/api/lists/\(list.id)", body: ListPayload(id: nil, name: list.name, description: list.description, emoji: list.emoji ?? ""))
    }

    func deleteList(id: String) async throws {
        do { try await sendNoContent("DELETE", "/api/lists/\(id)") } catch let error as APIError where error.status == 404 {}
    }

    func setMembership(tipId: String, listIds: Set<String>) async throws -> [SavedList] {
        try await send("PUT", "/api/tips/\(tipId)/lists", body: ["listIds": Array(listIds).sorted()])
    }

    func removeTip(listId: String, tipId: String) async throws -> SavedList {
        try await send("DELETE", "/api/lists/\(listId)/tips/\(tipId)", body: nil as String?)
    }

    func updateSettings(_ settings: AppSettings) async throws -> AppSettings {
        try await send("PUT", "/api/settings", body: settings)
    }

    func setChecklist(tipId: String, checked: Set<Int>) async throws {
        let _: ChecklistResponse = try await send("PUT", "/api/tips/\(tipId)/checklist", body: ["checked": Array(checked).sorted()])
    }

    // MARK: - Plumbing

    private struct ListPayload: Encodable {
        let id: String?
        let name: String
        let description: String
        let emoji: String?
    }

    private struct ChecklistResponse: Decodable {
        let tipId: String
        let checked: [Int]
    }

    private struct ErrorBody: Decodable { let error: String }

    private func get<T: Decodable>(_ path: String) async throws -> T {
        try decoder.decode(T.self, from: try await perform("GET", path, body: nil))
    }

    private func send<T: Decodable, B: Encodable>(_ method: String, _ path: String, body: B?) async throws -> T {
        let data = try body.map { try encoder.encode($0) }
        return try decoder.decode(T.self, from: try await perform(method, path, body: data))
    }

    private func sendNoContent(_ method: String, _ path: String) async throws {
        _ = try await perform(method, path, body: nil)
    }

    private func perform(_ method: String, _ path: String, body: Data?) async throws -> Data {
        var request = URLRequest(url: baseURL.appending(path: path))
        request.httpMethod = method
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        request.setValue(deviceId, forHTTPHeaderField: "X-Device-Id")
        if let body {
            request.httpBody = body
            request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        }
        let (data, response) = try await session.data(for: request)
        let status = (response as? HTTPURLResponse)?.statusCode ?? 0
        guard (200..<300).contains(status) else {
            let message = (try? decoder.decode(ErrorBody.self, from: data))?.error ?? "HTTP \(status)"
            throw APIError(status: status, message: message)
        }
        return data
    }
}
