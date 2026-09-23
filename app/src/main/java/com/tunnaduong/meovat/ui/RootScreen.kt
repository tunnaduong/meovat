package com.tunnaduong.meovat.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.tunnaduong.meovat.data.AppState
import com.tunnaduong.meovat.ui.components.AppTab
import com.tunnaduong.meovat.ui.components.AppTabBar
import com.tunnaduong.meovat.ui.screens.AboutScreen
import com.tunnaduong.meovat.ui.screens.HomeScreen
import com.tunnaduong.meovat.ui.screens.LanguageScreen
import com.tunnaduong.meovat.ui.screens.SavedListDetailScreen
import com.tunnaduong.meovat.ui.screens.SavedScreen
import com.tunnaduong.meovat.ui.screens.SettingsScreen
import com.tunnaduong.meovat.ui.screens.TipDetailScreen
import com.tunnaduong.meovat.ui.screens.TipInfoScreen
import com.tunnaduong.meovat.ui.screens.TipListScreen
import com.tunnaduong.meovat.ui.theme.AppColor

private val rootRoutes = setOf("home", "saved", "settings")

/** One NavHost with a nested graph per tab, under a floating tab bar that hides on full-bleed tip screens. */
@Composable
fun RootScreen(vm: AppViewModel = viewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val selectedTab = AppTab.entries.firstOrNull { tab ->
        backStackEntry?.destination?.hierarchy?.any { it.route == tab.route } == true
    } ?: AppTab.HOME
    val tabBarVisible = currentRoute == null || !(currentRoute.contains("/tip/") || currentRoute.contains("/guide/"))

    Box(Modifier.fillMaxSize().background(AppColor.Canvas)) {
        NavHost(
            navController = navController,
            startDestination = AppTab.HOME.route,
            modifier = Modifier.fillMaxSize(),
            enterTransition = { pushEnter() },
            exitTransition = { pushExit() },
            popEnterTransition = { popEnter() },
            popExitTransition = { popExit() },
        ) {
            navigation(startDestination = "home", route = AppTab.HOME.route) {
                composable("home") {
                    HomeScreen(vm, state, onCategory = { navController.navigate("home/category/$it") })
                }
                composable("home/category/{id}") { entry ->
                    TipListScreen(
                        categoryId = entry.id(),
                        vm = vm,
                        state = state,
                        onBack = { navController.popBackStack() },
                        onTip = { navController.navigate("home/tip/$it") },
                    )
                }
                tipRoutes("home", vm, state, navController)
            }
            navigation(startDestination = "saved", route = AppTab.SAVED.route) {
                composable("saved") {
                    SavedScreen(vm, state, onList = { navController.navigate("saved/list/$it") })
                }
                composable("saved/list/{id}") { entry ->
                    SavedListDetailScreen(
                        listId = entry.id(),
                        vm = vm,
                        state = state,
                        onBack = { navController.popBackStack() },
                        onTip = { navController.navigate("saved/tip/$it") },
                    )
                }
                tipRoutes("saved", vm, state, navController)
            }
            navigation(startDestination = "settings", route = AppTab.SETTINGS.route) {
                composable("settings") {
                    SettingsScreen(
                        vm = vm,
                        state = state,
                        onLanguage = { navController.navigate("settings/language") },
                        onAbout = { navController.navigate("settings/about") },
                    )
                }
                composable("settings/language") { LanguageScreen(vm, state, onBack = { navController.popBackStack() }) }
                composable("settings/about") { AboutScreen(state, onBack = { navController.popBackStack() }) }
            }
        }

        AnimatedVisibility(
            visible = tabBarVisible,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(tween(250)) { it } + fadeIn(tween(250)),
            exit = slideOutVertically(tween(250)) { it } + fadeOut(tween(250)),
        ) {
            AppTabBar(
                selected = selectedTab,
                l10n = state.l10n,
                onSelect = { tab ->
                    navController.navigate(tab.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier.navigationBarsPadding(),
            )
        }
    }
}

/** Tip info + step guide are reachable from both the Home and Saved tabs, so each graph gets its own copy. */
private fun NavGraphBuilder.tipRoutes(prefix: String, vm: AppViewModel, state: AppState, navController: NavHostController) {
    composable("$prefix/tip/{id}") { entry ->
        TipInfoScreen(
            tipId = entry.id(),
            vm = vm,
            state = state,
            onBack = { navController.popBackStack() },
            onGuide = { navController.navigate("$prefix/guide/$it") },
        )
    }
    composable("$prefix/guide/{id}") { entry ->
        TipDetailScreen(tipId = entry.id(), vm = vm, state = state, onClose = { navController.popBackStack() })
    }
}

private fun NavBackStackEntry.id(): String = arguments?.getString("id").orEmpty()

private fun AnimatedContentTransitionScope<NavBackStackEntry>.isTabSwitch() =
    targetState.destination.route in rootRoutes && initialState.destination.route in rootRoutes

private fun AnimatedContentTransitionScope<NavBackStackEntry>.pushEnter(): EnterTransition =
    if (isTabSwitch()) fadeIn(tween(200)) else slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300))

private fun AnimatedContentTransitionScope<NavBackStackEntry>.pushExit(): ExitTransition =
    if (isTabSwitch()) fadeOut(tween(200)) else slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300)) { it / 4 }

private fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnter(): EnterTransition =
    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300)) { it / 4 }

private fun AnimatedContentTransitionScope<NavBackStackEntry>.popExit(): ExitTransition =
    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300))
