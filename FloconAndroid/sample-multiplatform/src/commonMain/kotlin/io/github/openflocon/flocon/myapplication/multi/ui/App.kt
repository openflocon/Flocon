package io.github.openflocon.flocon.myapplication.multi.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.myapplication.multi.dashboard.initializeDashboard
import io.github.openflocon.flocon.myapplication.multi.featureflags.initializeFeatureFlags
import io.github.openflocon.flocon.myapplication.multi.preferences.SamplePreferences
import io.github.openflocon.flocon.myapplication.multi.ui.theme.SampleTheme
import io.github.openflocon.flocon.plugins.sharedprefs.preferencesPlugin

/** The sample: a home listing the plugins, and one screen per plugin (see [SampleNavigation]). */
@Composable
fun App() {
    LaunchedEffect(Unit) {
        initializeDashboard()
        initializeFeatureFlags()
        Flocon.preferencesPlugin.register(SamplePreferences)
    }

    SampleTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SampleTheme.colors.bg)
                .safeDrawingPadding(),
        ) {
            SampleNavigation(backStack = rememberSampleBackStack())
        }
    }
}
