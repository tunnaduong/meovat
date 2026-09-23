import SwiftUI

struct SavedView: View {
    @Environment(AppStore.self) private var store
    @State private var showCreate = false
    @State private var listToEdit: SavedList?
    @State private var listToDelete: SavedList?

    var body: some View {
        let l = store.l10n
        VStack(spacing: 0) {
            PageHeader(title: l.savedTitle, subtitle: l.savedSubtitle) {
                Button { showCreate = true } label: {
                    AppIcon(name: "ic_plus", size: 20)
                        .foregroundStyle(.white)
                        .frame(width: 44, height: 44)
                        .background(AppColor.primary, in: Circle())
                }
                .buttonStyle(.plain)
            }
            ScrollView {
                LazyVStack(spacing: 12) {
                    ForEach(store.lists) { list in
                        NavigationLink(value: SavedRoute.list(list.id)) {
                            SavedListCard(list: list)
                        }
                        .buttonStyle(.plain)
                        .contextMenu {
                            Button(l.edit) { listToEdit = list }
                            Button(l.delete, role: .destructive) { listToDelete = list }
                        }
                    }
                }
                .padding(.horizontal, 20)
                .padding(.top, 20)
                .padding(.bottom, 120)
            }
        }
        .background(AppColor.canvas)
        .toolbar(.hidden, for: .navigationBar)
        .sheet(isPresented: $showCreate) {
            ListFormSheet(mode: .create)
        }
        .sheet(item: $listToEdit) { list in
            ListFormSheet(mode: .edit(list))
        }
        .confirmationDialog(l.deleteConfirm, isPresented: Binding(
            get: { listToDelete != nil },
            set: { if !$0 { listToDelete = nil } }
        ), titleVisibility: .visible) {
            Button(l.delete, role: .destructive) {
                if let list = listToDelete { store.deleteList(id: list.id) }
            }
            Button(l.cancel, role: .cancel) {}
        }
    }
}
