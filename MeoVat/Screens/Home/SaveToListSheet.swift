import SwiftUI

/// "Lưu mẹo vặt" bottom sheet: pick which saved lists contain the tip.
struct SaveToListSheet: View {
    let tip: Tip
    @Environment(AppStore.self) private var store
    @Environment(\.dismiss) private var dismiss
    @State private var selection: Set<String> = []
    @State private var showCreate = false

    var body: some View {
        let l = store.l10n
        SheetScaffold(icon: "ic_bookmark_plus", title: l.saveTipTitle) {
            Text(l.chooseList)
                .font(AppFont.textMD)
                .foregroundStyle(AppColor.body)
            OptionList(items: store.lists) { list in
                OptionRow(emoji: list.emoji ?? "📁", title: list.name, selected: selection.contains(list.id)) {
                    withAnimation(.snappy(duration: 0.2)) {
                        if !selection.insert(list.id).inserted { selection.remove(list.id) }
                    }
                }
            }
            Button { showCreate = true } label: {
                HStack(spacing: 8) {
                    AppIcon(name: "ic_plus", size: 16)
                    Text(l.createList)
                        .font(AppFont.textSMMedium)
                }
                .foregroundStyle(AppColor.primary)
            }
            .buttonStyle(.plain)
            PrimaryButton(title: l.saveChanges) {
                store.setMembership(of: tip.id, lists: selection)
                dismiss()
            }
            .padding(.top, 8)
        }
        .onAppear { selection = store.listIds(containing: tip.id) }
        .sheet(isPresented: $showCreate) {
            ListFormSheet(mode: .create) { created in selection.insert(created.id) }
        }
        .fittedSheet()
    }
}
