import SwiftUI

struct HomeView: View {
    @Environment(AppStore.self) private var store

    var body: some View {
        let l = store.l10n
        VStack(spacing: 0) {
            PageHeader(title: l.homeTitle, subtitle: l.homeSubtitle)
            ScrollView {
                LazyVStack(spacing: 12) {
                    ForEach(store.categories) { category in
                        NavigationLink(value: HomeRoute.category(category.id)) {
                            CategoryCard(category: category)
                        }
                        .buttonStyle(.plain)
                    }
                }
                .padding(.horizontal, 20)
                .padding(.top, 20)
                .padding(.bottom, 120)
            }
        }
        .background(AppColor.canvas)
        .toolbar(.hidden, for: .navigationBar)
    }
}
