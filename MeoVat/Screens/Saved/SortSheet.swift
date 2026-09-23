import SwiftUI

/// "Sắp xếp" bottom sheet.
struct SortSheet: View {
    @Environment(AppStore.self) private var store
    @Environment(\.dismiss) private var dismiss
    @State private var selection: SortOrder = .newest

    private struct Option: Identifiable {
        let id: SortOrder
        let emoji: String
        let title: String
    }

    var body: some View {
        let l = store.l10n
        let options = [
            Option(id: .newest, emoji: "🕒", title: l.sortNewest),
            Option(id: .alphabetical, emoji: "🔤", title: l.sortAlphabetical),
        ]
        SheetScaffold(icon: "ic_sort", title: l.sortTitle) {
            Text(l.sortChoose)
                .font(AppFont.textMD)
                .foregroundStyle(AppColor.body)
            OptionList(items: options) { option in
                OptionRow(emoji: option.emoji, title: option.title, selected: selection == option.id) {
                    withAnimation(.snappy(duration: 0.2)) { selection = option.id }
                }
            }
            PrimaryButton(title: l.saveChanges) {
                store.settings.sortOrder = selection
                dismiss()
            }
            .padding(.top, 8)
        }
        .onAppear { selection = store.settings.sortOrder }
        .fittedSheet()
    }
}
