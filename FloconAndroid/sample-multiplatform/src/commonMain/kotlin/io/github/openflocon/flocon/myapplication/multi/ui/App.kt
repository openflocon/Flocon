package io.github.openflocon.flocon.myapplication.multi.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.myapplication.multi.dashboard.initializeDashboard
import io.github.openflocon.flocon.myapplication.multi.featureflags.initializeFeatureFlags
import io.github.openflocon.flocon.myapplication.multi.preferences.SamplePreferences
import io.github.openflocon.flocon.myapplication.multi.ui.screens.AnalyticsScreen
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
import io.github.openflocon.flocon.myapplication.multi.ui.theme.SampleTheme
import io.github.openflocon.flocon.plugins.sharedprefs.preferencesPlugin

/**
 * The sample: a home listing the plugins, and one screen per plugin. [current] is the open
 * screen, `null` being the home.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun App() {
    LaunchedEffect(Unit) {
        initializeDashboard()
        initializeFeatureFlags()
        Flocon.preferencesPlugin.register(SamplePreferences)
    }

    SampleTheme {
        var current by remember { mutableStateOf<SampleScreen?>(null) }
        val onBack = { current = null }

        BackHandler(enabled = current != null, onBack = onBack)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SampleTheme.colors.bg)
                .safeDrawingPadding(),
        ) {
            when (current) {
                null -> HomeScreen(onOpen = { current = it })
                SampleScreen.Network -> NetworkScreen(onBack)
                SampleScreen.Database -> DatabaseScreen(onBack)
                SampleScreen.Analytics -> AnalyticsScreen(onBack)
                SampleScreen.FeatureFlags -> FeatureFlagsScreen(onBack)
                SampleScreen.Preferences -> PreferencesScreen(onBack)
                SampleScreen.Files -> FilesScreen(onBack)
                SampleScreen.Dashboard -> DashboardScreen(onBack)
                SampleScreen.Deeplinks -> DeeplinksScreen(onBack)
                SampleScreen.Images -> ImagesScreen(onBack)
                SampleScreen.Crash -> CrashScreen(onBack)
            }
        }
    }
}
