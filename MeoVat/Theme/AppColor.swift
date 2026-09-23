import SwiftUI

extension Color {
    init(hex: UInt32) {
        self.init(
            red: Double((hex >> 16) & 0xFF) / 255,
            green: Double((hex >> 8) & 0xFF) / 255,
            blue: Double(hex & 0xFF) / 255
        )
    }
}

/// Design tokens from the Figma file ("content/*", "surface/*", "border/*").
enum AppColor {
    static let primary = Color(hex: 0xF36704)        // content/primary/default
    static let primarySubtle = Color(hex: 0xFFE1CC)  // surface/primary/subtle
    static let heading = Color(hex: 0x1D2939)        // content/neutral/normal/heading
    static let body = Color(hex: 0x344054)           // content/neutral/normal/body
    static let caption = Color(hex: 0x667085)        // content/neutral/normal/caption
    static let disabled = Color(hex: 0x98A2B3)       // content/disabled/default
    static let border = Color(hex: 0xD0D5DD)         // border/neutral/default
    static let divider = Color(hex: 0xE4E7EC)
    static let canvas = Color.white                  // surface/bg/canva/default
    static let canvasSecondary = Color(hex: 0xF2F4F7) // surface/bg/canva/secondary
    static let surface = Color.white                 // surface/bg/container/surface
}
