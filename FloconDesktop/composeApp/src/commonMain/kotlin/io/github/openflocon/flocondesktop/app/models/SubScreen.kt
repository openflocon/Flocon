package io.github.openflocon.flocondesktop.app.models

import io.github.openflocon.flocondesktop.features.analytics.AnalyticsRoute
import io.github.openflocon.flocondesktop.features.network.NetworkRoute
import io.github.openflocon.flocondesktop.main.ui.model.SubScreen
import io.github.openflocon.navigation.FloconRoute

val SubScreen.route: FloconRoute
    get() = when (this) {
        SubScreen.Dashboard -> TODO()
        SubScreen.Network -> NetworkRoute
        SubScreen.Images -> TODO()
        SubScreen.Database -> TODO()
        SubScreen.Files -> TODO()
        SubScreen.SharedPreferences -> TODO()
        SubScreen.Analytics -> AnalyticsRoute
        SubScreen.Tables -> TODO()
        SubScreen.Settings -> TODO()
        SubScreen.Deeplinks -> TODO()
    }
