import SwiftUI

/// Template icon from the asset catalog; every icon is normalized to a 24x24 box so `size` scales the whole glyph.
struct AppIcon: View {
    let name: String
    var size: CGFloat = 24

    var body: some View {
        Image(name)
            .renderingMode(.template)
            .resizable()
            .scaledToFit()
            .frame(width: size, height: size)
    }
}
