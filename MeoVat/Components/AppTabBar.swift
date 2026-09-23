import SwiftUI

enum AppTab: CaseIterable {
    case home, saved, settings

    var icon: String {
        switch self {
        case .home: "ic_house"
        case .saved: "ic_bookmark_check"
        case .settings: "ic_settings"
        }
    }
}

/// Floating pill tab bar ("Frame 90" in the design).
struct AppTabBar: View {
    @Binding var selected: AppTab
    @Environment(AppStore.self) private var store

    var body: some View {
        let l = store.l10n
        HStack(spacing: 0) {
            ForEach(AppTab.allCases, id: \.self) { tab in
                let isSelected = tab == selected
                Button {
                    withAnimation(.snappy(duration: 0.25)) { selected = tab }
                } label: {
                    VStack(spacing: 2) {
                        AppIcon(name: tab.icon, size: 28)
                        Text(label(for: tab, l))
                            .font(isSelected ? AppFont.textXSMedium : AppFont.textXS)
                    }
                    .foregroundStyle(isSelected ? AppColor.primary : AppColor.disabled)
                    .frame(width: 97)
                    .padding(.vertical, 4)
                    .background(isSelected ? AppColor.primarySubtle : .clear, in: Capsule())
                }
                .buttonStyle(.plain)
            }
        }
        .padding(.horizontal, 8)
        .frame(height: 68)
        .background(AppColor.canvasSecondary, in: Capsule())
    }

    private func label(for tab: AppTab, _ l: L10n) -> String {
        switch tab {
        case .home: l.tabHome
        case .saved: l.tabSaved
        case .settings: l.tabSettings
        }
    }
}
