import SwiftUI

struct LanguageView: View {
    @Environment(AppStore.self) private var store
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        let l = store.l10n
        VStack(spacing: 0) {
            SubPageHeader(title: l.languageTitle, onBack: { dismiss() })
            ScrollView {
                VStack(alignment: .leading, spacing: 8) {
                    Text(l.chooseLanguage)
                        .font(AppFont.textLGMedium)
                        .foregroundStyle(AppColor.heading)
                        .padding(.bottom, 4)
                    ForEach(Array(AppLanguage.allCases.enumerated()), id: \.element) { index, language in
                        Button {
                            withAnimation(.snappy(duration: 0.2)) { store.settings.language = language }
                        } label: {
                            HStack(spacing: 12) {
                                Text(language.flag)
                                    .font(.system(size: 22))
                                Text(l.languageName(language))
                                    .font(AppFont.textMD)
                                    .foregroundStyle(AppColor.heading)
                                Spacer(minLength: 0)
                                RadioIndicator(selected: store.settings.language == language)
                            }
                            .frame(height: 48)
                            .contentShape(Rectangle())
                        }
                        .buttonStyle(.plain)
                        if index < AppLanguage.allCases.count - 1 {
                            Rectangle()
                                .fill(AppColor.divider)
                                .frame(height: 1)
                        }
                    }
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
}
