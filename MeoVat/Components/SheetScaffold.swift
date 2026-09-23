import SwiftUI

/// Bottom-sheet body: custom grabber, orange icon + title, then content.
struct SheetScaffold<Content: View>: View {
    let icon: String
    let title: String
    @ViewBuilder var content: Content

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Capsule()
                .fill(AppColor.border)
                .frame(width: 90, height: 4)
                .frame(maxWidth: .infinity)
                .padding(.top, 8)
                .padding(.bottom, 8)
            HStack(spacing: 12) {
                AppIcon(name: icon, size: 24)
                    .foregroundStyle(AppColor.primary)
                Text(title)
                    .font(AppFont.headingMD)
                    .foregroundStyle(AppColor.primary)
            }
            content
        }
        .padding(.horizontal, 20)
        .padding(.bottom, 8)
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

/// Sizes a `.sheet` to its content height and applies the design's 24pt corner radius.
struct FittedSheet: ViewModifier {
    @State private var height: CGFloat = 320

    func body(content: Content) -> some View {
        content
            .fixedSize(horizontal: false, vertical: true)
            .onGeometryChange(for: CGFloat.self, of: { $0.size.height }) { height = $0 }
            .frame(maxHeight: .infinity, alignment: .top)
            .presentationDetents([.height(height)])
            .presentationDragIndicator(.hidden)
            .presentationCornerRadius(24)
            .presentationBackground(AppColor.canvas)
    }
}

extension View {
    func fittedSheet() -> some View { modifier(FittedSheet()) }
}
