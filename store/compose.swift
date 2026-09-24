import Foundation
import CoreGraphics
import CoreText
import ImageIO
import UniformTypeIdentifiers

// Usage: compose.swift feature <icon.png> <out.png>
//        compose.swift shot <screenshot.png> <caption> <out.png>
let fontDir = ProcessInfo.processInfo.environment["FONT_DIR"] ?? "."
for name in ["be_vietnam_pro_semibold.ttf", "be_vietnam_pro_regular.ttf", "be_vietnam_pro_medium.ttf", "inter_semibold.ttf"] {
    CTFontManagerRegisterFontsForURL(URL(fileURLWithPath: "\(fontDir)/\(name)") as CFURL, .process, nil)
}

let srgb = CGColorSpace(name: CGColorSpace.sRGB)!
func rgb(_ hex: UInt32, _ alpha: CGFloat = 1) -> CGColor {
    CGColor(colorSpace: srgb, components: [CGFloat((hex >> 16) & 0xff) / 255, CGFloat((hex >> 8) & 0xff) / 255, CGFloat(hex & 0xff) / 255, alpha])!
}
func context(_ w: Int, _ h: Int) -> CGContext {
    CGContext(data: nil, width: w, height: h, bitsPerComponent: 8, bytesPerRow: 0, space: srgb, bitmapInfo: CGImageAlphaInfo.noneSkipLast.rawValue)!
}
func image(_ path: String) -> CGImage {
    CGImageSourceCreateImageAtIndex(CGImageSourceCreateWithURL(URL(fileURLWithPath: path) as CFURL, nil)!, 0, nil)!
}
func save(_ ctx: CGContext, _ path: String) {
    let dest = CGImageDestinationCreateWithURL(URL(fileURLWithPath: path) as CFURL, UTType.png.identifier as CFString, 1, nil)!
    CGImageDestinationAddImage(dest, ctx.makeImage()!, nil)
    CGImageDestinationFinalize(dest)
}
/// Rect given in top-left coordinates, converted to CoreGraphics' bottom-left origin.
func rect(_ ctx: CGContext, _ x: CGFloat, _ yTop: CGFloat, _ w: CGFloat, _ h: CGFloat) -> CGRect {
    CGRect(x: x, y: CGFloat(ctx.height) - yTop - h, width: w, height: h)
}
func gradientBackground(_ ctx: CGContext) {
    let g = CGGradient(colorsSpace: srgb, colors: [rgb(0xFF8A3D), rgb(0xE85D00)] as CFArray, locations: [0, 1])!
    ctx.drawLinearGradient(g, start: CGPoint(x: 0, y: CGFloat(ctx.height)), end: CGPoint(x: CGFloat(ctx.width), y: 0), options: [])
    let glow = CGGradient(colorsSpace: srgb, colors: [rgb(0xFFFFFF, 0.18), rgb(0xFFFFFF, 0)] as CFArray, locations: [0, 1])!
    ctx.drawRadialGradient(glow, startCenter: CGPoint(x: CGFloat(ctx.width) * 0.5, y: CGFloat(ctx.height) * 0.8), startRadius: 0,
                           endCenter: CGPoint(x: CGFloat(ctx.width) * 0.5, y: CGFloat(ctx.height) * 0.8), endRadius: CGFloat(ctx.width) * 0.7, options: [])
}
func text(_ ctx: CGContext, _ string: String, font: String, size: CGFloat, color: CGColor, in r: CGRect, align: CTTextAlignment = .center, lineHeight: CGFloat = 1.15) {
    var alignment = align
    var spacing = size * (lineHeight - 1)
    let settings = [
        CTParagraphStyleSetting(spec: .alignment, valueSize: MemoryLayout<CTTextAlignment>.size, value: &alignment),
        CTParagraphStyleSetting(spec: .lineSpacingAdjustment, valueSize: MemoryLayout<CGFloat>.size, value: &spacing),
    ]
    let style = CTParagraphStyleCreate(settings, settings.count)
    let attributes: [NSAttributedString.Key: Any] = [
        kCTFontAttributeName as NSAttributedString.Key: CTFontCreateWithName(font as CFString, size, nil),
        kCTForegroundColorAttributeName as NSAttributedString.Key: color,
        kCTParagraphStyleAttributeName as NSAttributedString.Key: style,
    ]
    let framesetter = CTFramesetterCreateWithAttributedString(NSAttributedString(string: string, attributes: attributes))
    let frame = CTFramesetterCreateFrame(framesetter, CFRange(location: 0, length: 0), CGPath(rect: r, transform: nil), nil)
    CTFrameDraw(frame, ctx)
}
func roundedImage(_ ctx: CGContext, _ img: CGImage, in r: CGRect, radius: CGFloat, border: CGColor? = nil, shadow: Bool = true) {
    let path = CGPath(roundedRect: r, cornerWidth: radius, cornerHeight: radius, transform: nil)
    if shadow {
        ctx.saveGState()
        ctx.setShadow(offset: CGSize(width: 0, height: -24), blur: 60, color: rgb(0x000000, 0.35))
        ctx.setFillColor(rgb(0x000000, 1))
        ctx.addPath(path); ctx.fillPath()
        ctx.restoreGState()
    }
    ctx.saveGState()
    ctx.addPath(path); ctx.clip()
    ctx.draw(img, in: r)
    ctx.restoreGState()
    if let border {
        ctx.saveGState()
        ctx.setStrokeColor(border); ctx.setLineWidth(3)
        ctx.addPath(path); ctx.strokePath()
        ctx.restoreGState()
    }
}

let args = CommandLine.arguments
switch args[1] {
case "feature":
    // 1024x500: app icon tile on the left, name + tagline on the right.
    let ctx = context(1024, 500)
    gradientBackground(ctx)
    roundedImage(ctx, image(args[2]), in: rect(ctx, 96, 106, 288, 288), radius: 64, border: rgb(0xFFFFFF, 0.35))
    text(ctx, "Mẹo Vặt", font: "Inter-SemiBold", size: 118, color: rgb(0xFFFFFF), in: rect(ctx, 440, 128, 560, 150), align: .left)
    text(ctx, "Mẹo nhỏ, hiệu quả lớn", font: "BeVietnamPro-Medium", size: 42, color: rgb(0xFFFFFF, 0.95), in: rect(ctx, 446, 276, 560, 60), align: .left)
    text(ctx, "Hướng dẫn từng bước, dễ làm tại nhà", font: "BeVietnamPro-Regular", size: 28, color: rgb(0xFFFFFF, 0.85), in: rect(ctx, 446, 340, 560, 60), align: .left)
    save(ctx, args[3])
case "shot":
    // 1080x1920: caption on top, phone screenshot bleeding off the bottom.
    let ctx = context(1080, 1920)
    gradientBackground(ctx)
    text(ctx, args[3], font: "BeVietnamPro-SemiBold", size: 64, color: rgb(0xFFFFFF), in: rect(ctx, 80, 120, 920, 220))
    let shot = image(args[2])
    let w: CGFloat = 820, h = w * CGFloat(shot.height) / CGFloat(shot.width)
    roundedImage(ctx, shot, in: rect(ctx, (1080 - w) / 2, 400, w, h), radius: 64, border: rgb(0xFFFFFF, 0.5))
    save(ctx, args[4])
default:
    fatalError("unknown mode")
}
