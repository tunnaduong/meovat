import SwiftUI

/// Tips of one category with search, tag chips and a sort sheet behind the filter button.
struct TipListView: View {
    let categoryId: String
    @Environment(AppStore.self) private var store
    @Environment(\.dismiss) private var dismiss
    @State private var query = ""
    @State private var selectedTag: String?
    @State private var tipToSave: Tip?
    @State private var showSort = false

    var body: some View {
        let l = store.l10n
        if let category = store.category(id: categoryId) {
            let tags = store.tags(in: category)
            let tips = store.applySort(store.tips(in: category)).filter { tip in
                (selectedTag == nil || tip.tag == selectedTag) && matches(tip)
            }
            VStack(spacing: 0) {
                SubPageHeader(title: l.categoryTitle(category.name), onBack: { dismiss() }) {
                    SearchRow(text: $query, placeholder: l.searchPlaceholder) { showSort = true }
                }
                ScrollView {
                    VStack(alignment: .leading, spacing: 20) {
                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack(spacing: 8) {
                                ForEach(tags, id: \.self) { tag in
                                    FilterChip(title: tag, selected: selectedTag == tag) {
                                        withAnimation(.snappy(duration: 0.2)) {
                                            selectedTag = selectedTag == tag ? nil : tag
                                        }
                                    }
                                }
                            }
                            .padding(.horizontal, 20)
                        }
                        LazyVStack(spacing: 12) {
                            ForEach(tips) { tip in
                                NavigationLink(value: HomeRoute.tipInfo(tip.id)) {
                                    TipCard(tip: tip) {
                                        Button(l.saveTipTitle) { tipToSave = tip }
                                    }
                                }
                                .buttonStyle(.plain)
                            }
                            if tips.isEmpty {
                                Text(l.noResults)
                                    .font(AppFont.textMD)
                                    .foregroundStyle(AppColor.caption)
                                    .padding(.top, 40)
                            }
                        }
                        .padding(.horizontal, 20)
                    }
                    .padding(.top, 20)
                    .padding(.bottom, 120)
                }
                .scrollDismissesKeyboard(.immediately)
            }
            .background(AppColor.canvas)
            .toolbar(.hidden, for: .navigationBar)
            .sheet(item: $tipToSave) { tip in
                SaveToListSheet(tip: tip)
            }
            .sheet(isPresented: $showSort) {
                SortSheet()
            }
        }
    }

    private func matches(_ tip: Tip) -> Bool {
        let q = query.trimmingCharacters(in: .whitespaces)
        return q.isEmpty
            || tip.title.localizedCaseInsensitiveContains(q)
            || tip.subtitle.localizedCaseInsensitiveContains(q)
    }
}
