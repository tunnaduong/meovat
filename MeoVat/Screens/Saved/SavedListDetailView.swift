import SwiftUI

/// Contents of one saved list; the filter button opens the sort sheet, the gear edits the list.
struct SavedListDetailView: View {
    let listId: String
    @Environment(AppStore.self) private var store
    @Environment(\.dismiss) private var dismiss
    @State private var query = ""
    @State private var showSort = false
    @State private var showEdit = false
    @State private var tipToSave: Tip?

    var body: some View {
        let l = store.l10n
        if let list = store.list(id: listId) {
            let tips = store.applySort(store.tips(in: list)).filter(matches)
            VStack(spacing: 0) {
                SubPageHeader(title: list.name, onBack: { dismiss() }) {
                    SearchRow(text: $query, placeholder: l.searchPlaceholder) { showSort = true }
                } trailing: {
                    Button { showEdit = true } label: {
                        AppIcon(name: "ic_settings_2", size: 22)
                            .foregroundStyle(AppColor.primary)
                            .frame(width: 36, height: 36)
                    }
                    .buttonStyle(.plain)
                }
                ScrollView {
                    LazyVStack(spacing: 12) {
                        ForEach(tips) { tip in
                            NavigationLink(value: SavedRoute.tipInfo(tip.id)) {
                                TipCard(tip: tip) {
                                    Button(l.saveTipTitle) { tipToSave = tip }
                                    Button(l.removeFromList, role: .destructive) {
                                        withAnimation(.snappy(duration: 0.25)) {
                                            store.remove(tipId: tip.id, fromList: list.id)
                                        }
                                    }
                                }
                            }
                            .buttonStyle(.plain)
                        }
                        if tips.isEmpty {
                            Text(query.isEmpty ? l.emptyList : l.noResults)
                                .font(AppFont.textMD)
                                .foregroundStyle(AppColor.caption)
                                .padding(.top, 40)
                        }
                    }
                    .padding(.horizontal, 20)
                    .padding(.top, 20)
                    .padding(.bottom, 120)
                }
                .scrollDismissesKeyboard(.immediately)
            }
            .background(AppColor.canvas)
            .toolbar(.hidden, for: .navigationBar)
            .sheet(isPresented: $showSort) { SortSheet() }
            .sheet(isPresented: $showEdit) { ListFormSheet(mode: .edit(list)) }
            .sheet(item: $tipToSave) { tip in SaveToListSheet(tip: tip) }
        }
    }

    private func matches(_ tip: Tip) -> Bool {
        let q = query.trimmingCharacters(in: .whitespaces)
        return q.isEmpty
            || tip.title.localizedCaseInsensitiveContains(q)
            || tip.subtitle.localizedCaseInsensitiveContains(q)
    }
}
