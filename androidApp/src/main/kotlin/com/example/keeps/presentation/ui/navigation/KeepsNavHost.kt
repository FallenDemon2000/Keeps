package com.example.keeps.presentation.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.keeps.R
import com.example.keeps.data.scan.ScanResultsRepository
import com.example.keeps.presentation.home.HomeScreen
import com.example.keeps.presentation.results.ResultsScreen
import com.example.keeps.presentation.settings.SettingsScreen
import com.example.keeps.presentation.ui.icons.Icons
import com.example.keeps.presentation.ui.navigation.Screen.HomeScreen
import com.example.keeps.presentation.ui.navigation.Screen.ResultsScreen
import com.example.keeps.presentation.ui.navigation.Screen.SettingsScreen
import com.example.keeps.presentation.ui.theme.ThemeMode
import org.koin.compose.koinInject

private val TopLevelRoutes = listOf(HomeScreen, ResultsScreen, SettingsScreen)

/**
 * App-wide 3-tab navigation shell: a [Scaffold] with a [BottomNavBar] driving a
 * [NavHost] over [HomeScreen], [ResultsScreen], and [SettingsScreen]. Standard
 * save/restoreState bottom-nav pattern so each tab keeps its own scroll/selection
 * state when switching tabs.
 *
 * The one cross-cutting exception is theme mode: it's hoisted above this composable
 * (owned by `ThemeViewModel` in `KeepsApp`) since it must also drive the app's
 * root [com.example.keeps.presentation.ui.theme.KeepsTheme] wrapper.
 */
@Composable
fun KeepsNavHost(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val selectedIndex = TopLevelRoutes.indexOfFirst { it::class.qualifiedName == currentRoute }
        .coerceAtLeast(0)
    val resultsBadgeCount = resultsBadgeCount()

    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavBar(
                items = listOf(
                    BottomNavItem(
                        label = stringResource(id = R.string.home),
                        icon = Icons.Home,
                    ),
                    BottomNavItem(
                        label = stringResource(id = R.string.results),
                        icon = Icons.Search,
                        badgeCount = resultsBadgeCount,
                    ),
                    BottomNavItem(
                        label = stringResource(id = R.string.settings),
                        icon = Icons.Settings,
                    ),
                ),
                selectedIndex = selectedIndex,
                onItemSelected = { navigateToTopLevel(navController, TopLevelRoutes[it]) },
            )
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeScreen,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable<HomeScreen> {
                HomeScreen(
                    onScanCompleted = { navigateToTopLevel(navController, ResultsScreen) },
                )
            }
            composable<ResultsScreen> {
                ResultsScreen(
                    onStartNewScan = { navigateToTopLevel(navController, HomeScreen) },
                )
            }
            composable<SettingsScreen> {
                SettingsScreen(themeMode = themeMode, onThemeModeChange = onThemeModeChange)
            }
        }
    }
}

private fun navigateToTopLevel(navController: NavHostController, route: Any) {
    navController.navigate(route) {
        val startDestination = navController.graph.findStartDestination().id
        popUpTo(startDestination) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * Number of duplicate groups from the latest scan, shown as the Results tab
 * badge. Falls back to 0 in `@Preview` (no Koin instance is running there)
 * instead of resolving [ScanResultsRepository] via DI.
 */
@Composable
private fun resultsBadgeCount(): Int {
    if (LocalInspectionMode.current) return 0
    val scanResultsRepository = koinInject<ScanResultsRepository>()
    val groups by scanResultsRepository.groups.collectAsStateWithLifecycle()
    return groups.size
}
