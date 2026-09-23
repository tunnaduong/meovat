import SwiftUI

struct SettingsView: View {
    @Environment(AppStore.self) private var store

    var body: some View {
        @Bindable var store = store
        let l = store.l10n
        VStack(spacing: 0) {
            PageHeader(title: l.settingsTitle, subtitle: l.settingsSubtitle)
            ScrollView {
                VStack(alignment: .leading, spacing: 8) {
                    Text(l.basicSettings)
                        .font(AppFont.textLGSemibold)
                        .foregroundStyle(AppColor.primary)
                    settingsRow(icon: "ic_bell", title: l.notifications) {
                        Toggle("", isOn: $store.settings.notificationsEnabled)
                            .labelsHidden()
                            .tint(AppColor.primary)
                    }
                    divider
                    NavigationLink(value: SettingsRoute.language) {
                        settingsRow(icon: "ic_languages", title: l.languageTitle) {
                            HStack(spacing: 8) {
                                Text(store.settings.language.flag)
                                    .font(.system(size: 20))
                                Text(l.languageName(store.settings.language))
                                    .font(AppFont.textSM)
                                    .foregroundStyle(AppColor.caption)
                                AppIcon(name: "ic_chevron_right", size: 20)
                                    .foregroundStyle(AppColor.primary)
                            }
                        }
                    }
                    .buttonStyle(.plain)
                    divider
                    NavigationLink(value: SettingsRoute.about) {
                        settingsRow(icon: "ic_info", title: l.about) {
                            AppIcon(name: "ic_chevron_right", size: 20)
                                .foregroundStyle(AppColor.primary)
                        }
                    }
                    .buttonStyle(.plain)
                }
                .padding(16)
                .background(AppColor.canvasSecondary, in: RoundedRectangle(cornerRadius: 12))
                .padding(.horizontal, 20)
                .padding(.top, 20)
                .padding(.bottom, 120)
            }
        }
        .background(AppColor.canvas)
        .toolbar(.hidden, for: .navigationBar)
    }

    private var divider: some View {
        Rectangle()
            .fill(AppColor.divider)
            .frame(height: 1)
    }

    private func settingsRow<Trailing: View>(icon: String, title: String,
                                             @ViewBuilder trailing: () -> Trailing) -> some View {
        HStack(spacing: 12) {
            AppIcon(name: icon, size: 24)
                .foregroundStyle(AppColor.body)
            Text(title)
                .font(AppFont.textMD)
                .foregroundStyle(AppColor.heading)
            Spacer(minLength: 0)
            trailing()
        }
        .frame(height: 52)
        .contentShape(Rectangle())
    }
}
