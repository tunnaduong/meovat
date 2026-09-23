import Foundation

enum AppConfig {
    /// The Mẹo Vặt API on the Raspberry Pi (reachable over Tailscale).
    /// Override on a simulator with: `xcrun simctl spawn booted defaults write com.tunnaduong.meovat api_base_url http://host:port`
    static var apiBaseURL: URL {
        if let raw = UserDefaults.standard.string(forKey: "api_base_url"), let url = URL(string: raw) {
            return url
        }
        return URL(string: "http://100.102.160.98:8787")!
    }
}

/// Anonymous identity: a UUID generated once per install and sent as `X-Device-Id`.
enum DeviceIdentity {
    static let id: String = {
        let key = "device_id"
        if let existing = UserDefaults.standard.string(forKey: key) { return existing }
        let fresh = UUID().uuidString.lowercased()
        UserDefaults.standard.set(fresh, forKey: key)
        return fresh
    }()
}
