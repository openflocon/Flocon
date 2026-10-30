package io.github.openflocon.flocon.myapplication.multi.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import io.github.openflocon.flocon.myapplication.multi.ui.screens.AccessibilityScreen
import io.github.openflocon.flocon.myapplication.multi.ui.screens.AnalyticsScreen
import io.github.openflocon.flocon.myapplication.multi.ui.screens.ConnectionScreen
import io.github.openflocon.flocon.myapplication.multi.ui.screens.CrashScreen
import io.github.openflocon.flocon.myapplication.multi.ui.screens.DashboardScreen
import io.github.openflocon.flocon.myapplication.multi.ui.screens.DatabaseScreen
import io.github.openflocon.flocon.myapplication.multi.ui.screens.DeeplinksScreen
import io.github.openflocon.flocon.myapplication.multi.ui.screens.FeatureFlagsScreen
import io.github.openflocon.flocon.myapplication.multi.ui.screens.FilesScreen
import io.github.openflocon.flocon.myapplication.multi.ui.screens.HomeScreen
import io.github.openflocon.flocon.myapplication.multi.ui.screens.ImagesScreen
import io.github.openflocon.flocon.myapplication.multi.ui.screens.NetworkScreen
import io.github.openflocon.flocon.myapplication.multi.ui.screens.PreferencesScreen
import io.github.openflocon.flocon.myapplication.multi.ui.screens.WebViewScreen

/** A destination of the sample: the home, then one route per plugin screen. */
sealed interface SampleRoute {
    data object Home : SampleRoute
    data object Network : SampleRoute
    data object Database : SampleRoute
    data object Analytics : SampleRoute
    data object Accessibility : SampleRoute
    data object WebView : SampleRoute
    data object FeatureFlags : SampleRoute
    data object Preferences : SampleRoute
    data object Files : SampleRoute
    data object Dashboard : SampleRoute
    data object Deeplinks : SampleRoute
    data object Images : SampleRoute
    data object Crash : SampleRoute
    data object Connection : SampleRoute
}

val SampleScreen.route: SampleRoute
    get() = when (this) {
        SampleScreen.Network -> SampleRoute.Network
        SampleScreen.Database -> SampleRoute.Database
        SampleScreen.Analytics -> SampleRoute.Analytics
        SampleScreen.Accessibility -> SampleRoute.Accessibility
        SampleScreen.WebView -> SampleRoute.WebView
        SampleScreen.FeatureFlags -> SampleRoute.FeatureFlags
        SampleScreen.Preferences -> SampleRoute.Preferences
        SampleScreen.Files -> SampleRoute.Files
        SampleScreen.Dashboard -> SampleRoute.Dashboard
        SampleScreen.Deeplinks -> SampleRoute.Deeplinks
        SampleScreen.Images -> SampleRoute.Images
        SampleScreen.Crash -> SampleRoute.Crash
    }

/** The back stack of the sample, the first entry is always the home. */
@Composable
fun rememberSampleBackStack(): SnapshotStateList<SampleRoute> =
    remember { mutableStateListOf(SampleRoute.Home) }

/**
 * Navigation 3 host: handles the system back gesture, the transitions between screens and the
 * restoration of each screen's state.
 */
@Composable
fun SampleNavigation(backStack: SnapshotStateList<SampleRoute>) {
    val onBack: () -> Unit = { if (backStack.size > 1) backStack.removeLast() }

    NavDisplay(
        backStack = backStack,
        onBack = onBack,
        entryProvider = entryProvider {
            entry<SampleRoute.Home> {
                HomeScreen(
                    onOpen = { backStack.add(it.route) },
                    onOpenConnection = { backStack.add(SampleRoute.Connection) },
                )
            }
            entry<SampleRoute.Network> { NetworkScreen(onBack) }
            entry<SampleRoute.Database> { DatabaseScreen(onBack) }
            entry<SampleRoute.Analytics> { AnalyticsScreen(onBack) }
            entry<SampleRoute.Accessibility> { AccessibilityScreen(onBack) }
            entry<SampleRoute.WebView> { WebViewScreen(onBack) }
            entry<SampleRoute.FeatureFlags> { FeatureFlagsScreen(onBack) }
            entry<SampleRoute.Preferences> { PreferencesScreen(onBack) }
            entry<SampleRoute.Files> { FilesScreen(onBack) }
            entry<SampleRoute.Dashboard> { DashboardScreen(onBack) }
            entry<SampleRoute.Deeplinks> { DeeplinksScreen(onBack) }
            entry<SampleRoute.Images> { ImagesScreen(onBack) }
            entry<SampleRoute.Crash> { CrashScreen(onBack) }
            entry<SampleRoute.Connection> { ConnectionScreen(onBack) }
        },
    )
}
