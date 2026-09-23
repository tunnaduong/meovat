import SwiftUI

/// Large grey header with the 48pt Inter title (Home / Saved / Settings roots).
struct PageHeader<Trailing: View>: View {
    let title: String
    let subtitle: String
    @ViewBuilder var trailing: Trailing

    var body: some View {
        HStack(spacing: 12) {
            VStack(alignment: .leading, spacing: 4) {
                Text(title)
                    .font(AppFont.title5)
                    .tracking(-1.5)
                    .foregroundStyle(AppColor.primary)
                Text(subtitle)
                    .font(AppFont.textMD)
                    .foregroundStyle(AppColor.caption)
            }
            Spacer(minLength: 0)
            trailing
        }
        .padding(.horizontal, 20)
        .padding(.vertical, 24)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(AppColor.canvasSecondary.ignoresSafeArea(edges: .top))
    }
}

extension PageHeader where Trailing == EmptyView {
    init(title: String, subtitle: String) {
        self.init(title: title, subtitle: subtitle) { EmptyView() }
    }
}

/// Grey header with a back chevron and a 24pt orange title, plus optional extra rows (search etc.).
struct SubPageHeader<Content: View, Trailing: View>: View {
    let title: String
    let onBack: () -> Void
    @ViewBuilder var content: Content
    @ViewBuilder var trailing: Trailing

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack(spacing: 4) {
                Button(action: onBack) {
                    AppIcon(name: "ic_chevron_left", size: 24)
                        .foregroundStyle(AppColor.primary)
                        .frame(width: 36, height: 36)
                }
                .buttonStyle(.plain)
                Text(title)
                    .font(AppFont.headingMD)
                    .foregroundStyle(AppColor.primary)
                    .lineLimit(1)
                Spacer(minLength: 0)
                trailing
            }
            content
        }
        .padding(.horizontal, 20)
        .padding(.top, 16)
        .padding(.bottom, 24)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(AppColor.canvasSecondary.ignoresSafeArea(edges: .top))
    }
}

extension SubPageHeader where Trailing == EmptyView {
    init(title: String, onBack: @escaping () -> Void, @ViewBuilder content: () -> Content) {
        self.init(title: title, onBack: onBack, content: content) { EmptyView() }
    }
}

extension SubPageHeader where Content == EmptyView, Trailing == EmptyView {
    init(title: String, onBack: @escaping () -> Void) {
        self.init(title: title, onBack: onBack) { EmptyView() } trailing: { EmptyView() }
    }
}

/// Search field + round filter button row used under sub-page titles.
struct SearchRow: View {
    @Binding var text: String
    let placeholder: String
    let onFilter: () -> Void

    var body: some View {
        HStack(spacing: 12) {
            HStack(spacing: 8) {
                AppIcon(name: "ic_search", size: 20)
                    .foregroundStyle(AppColor.disabled)
                TextField(placeholder, text: $text)
                    .font(AppFont.textMD)
                    .foregroundStyle(AppColor.heading)
                    .tint(AppColor.primary)
                    .autocorrectionDisabled()
            }
            .padding(.horizontal, 16)
            .frame(height: 48)
            .background(AppColor.surface, in: RoundedRectangle(cornerRadius: 12))

            Button(action: onFilter) {
                AppIcon(name: "ic_filter", size: 20)
                    .foregroundStyle(AppColor.primary)
                    .frame(width: 48, height: 48)
                    .background(AppColor.surface, in: Circle())
            }
            .buttonStyle(.plain)
        }
    }
}
