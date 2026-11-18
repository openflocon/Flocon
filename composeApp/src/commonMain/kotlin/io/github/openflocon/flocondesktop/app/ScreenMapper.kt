package io.github.openflocon.flocondesktop.app

import io.github.openflocon.domain.screen.models.FloconScreen
import io.github.openflocon.flocondesktop.app.ui.model.SubScreen

internal fun FloconScreen.toSubScreen(): SubScreen = when (this) {
    FloconScreen.Dashboard -> SubScreen.Dashboard
    FloconScreen.Network -> SubScreen.Network
    FloconScreen.Images -> SubScreen.Images
    FloconScreen.Database -> SubScreen.Database
    FloconScreen.Files -> SubScreen.Files
    FloconScreen.SharedPreferences -> SubScreen.SharedPreferences
    FloconScreen.Analytics -> SubScreen.Analytics
    FloconScreen.FeatureFlags -> SubScreen.FeatureFlags
    FloconScreen.Accessibility -> SubScreen.Accessibility
    FloconScreen.Tables -> SubScreen.Tables
    FloconScreen.Deeplinks -> SubScreen.Deeplinks
    FloconScreen.AdbCommander -> SubScreen.AdbCommander
    FloconScreen.CrashReporter -> SubScreen.CrashReporter
    FloconScreen.Timeline -> SubScreen.Timeline
    FloconScreen.WebView -> SubScreen.WebView
}
