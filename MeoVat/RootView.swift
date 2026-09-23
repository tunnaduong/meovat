import SwiftUI

enum HomeRoute: Hashable {
    case category(String)
    case tipInfo(String)
}

enum SavedRoute: Hashable {
    case list(String)
    case tipInfo(String)
}

enum SettingsRoute: Hashable {
    case language
    case about
}

/// Three independent navigation stacks under a floating tab bar. The bar hides on full-bleed tip screens.
struct RootView: View {
    @State private var tab: AppTab = .home
    @State private var homePath: [HomeRoute] = []
    @State private var savedPath: [SavedRoute] = []
    @State private var settingsPath: [SettingsRoute] = []

    private var tabBarHidden: Bool {
        switch tab {
        case .home: homePath.contains { if case .tipInfo = $0 { true } else { false } }
        case .saved: savedPath.contains { if case .tipInfo = $0 { true } else { false } }
        case .settings: false
        }
    }

    var body: some View {
        ZStack(alignment: .bottom) {
            NavigationStack(path: $homePath) {
                HomeView()
                    .navigationDestination(for: HomeRoute.self) { route in
                        switch route {
                        case .category(let id): TipListView(categoryId: id)
                        case .tipInfo(let id): TipInfoView(tipId: id)
                        }
                    }
            }
            .opacity(tab == .home ? 1 : 0)
            .allowsHitTesting(tab == .home)

            NavigationStack(path: $savedPath) {
                SavedView()
                    .navigationDestination(for: SavedRoute.self) { route in
                        switch route {
                        case .list(let id): SavedListDetailView(listId: id)
                        case .tipInfo(let id): TipInfoView(tipId: id)
                        }
                    }
            }
            .opacity(tab == .saved ? 1 : 0)
            .allowsHitTesting(tab == .saved)

            NavigationStack(path: $settingsPath) {
                SettingsView()
                    .navigationDestination(for: SettingsRoute.self) { route in
                        switch route {
                        case .language: LanguageView()
                        case .about: AboutView()
                        }
                    }
            }
            .opacity(tab == .settings ? 1 : 0)
            .allowsHitTesting(tab == .settings)

            if !tabBarHidden {
                AppTabBar(selected: $tab)
                    .transition(.move(edge: .bottom).combined(with: .opacity))
            }
        }
        .animation(.snappy(duration: 0.25), value: tabBarHidden)
        .background(AppColor.canvas.ignoresSafeArea())
    }
}
