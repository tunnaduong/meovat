import SwiftUI

/// Typography tokens: "Be Vietnam Pro" for text styles, "Inter" for the Title 5 display style.
enum AppFont {
    enum Weight {
        case regular, medium, semibold, bold

        var postScriptName: String {
            switch self {
            case .regular: "BeVietnamPro-Regular"
            case .medium: "BeVietnamPro-Medium"
            case .semibold: "BeVietnamPro-SemiBold"
            case .bold: "BeVietnamPro-Bold"
            }
        }
    }

    static func text(_ size: CGFloat, _ weight: Weight = .regular) -> Font {
        .custom(weight.postScriptName, size: size)
    }

    /// Title 5 – Inter Semi Bold 48/52, letter spacing -1.5
    static let title5 = Font.custom("Inter-SemiBold", size: 48)

    static let textXS = text(12)
    static let textXSMedium = text(12, .medium)
    static let textSM = text(14)
    static let textSMMedium = text(14, .medium)
    static let textSMSemibold = text(14, .semibold)
    static let textMD = text(16)
    static let textMDMedium = text(16, .medium)
    static let textMDSemibold = text(16, .semibold)
    static let textLGMedium = text(18, .medium)
    static let textLGSemibold = text(18, .semibold)
    static let headingSM = text(20, .semibold)
    static let headingMD = text(24, .semibold)
}
