package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.app.ui.model.SubScreen
import io.github.openflocon.flocondesktop.features.analytics.AnalyticsRoutes
import io.github.openflocon.flocondesktop.features.network.NetworkRoutes
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRowUiModel
import io.github.openflocon.navigation.FloconRoute

internal sealed interface TimelineDestination {
    // the detail of the event, displayed in a panel on top of the timeline
    data class Panel(val route: FloconRoute) : TimelineDestination

    // the screen of the plugin: some plugins have no detail of their own
    data class Screen(val screen: SubScreen) : TimelineDestination
}

// null: nothing to open for this event
internal fun TimelineRowUiModel.toDestination(): TimelineDestination? = when (source) {
    TimelineSource.AppStart -> null
    TimelineSource.Network -> TimelineDestination.Panel(NetworkRoutes.Panel(requestId = id))
    TimelineSource.Analytics -> TimelineDestination.Panel(AnalyticsRoutes.Detail(id = id))
    TimelineSource.Crash -> TimelineDestination.Screen(SubScreen.CrashReporter)
    TimelineSource.Database -> TimelineDestination.Screen(SubScreen.Database)
    TimelineSource.Table -> TimelineDestination.Screen(SubScreen.Tables)
    TimelineSource.Deeplink -> TimelineDestination.Screen(SubScreen.Deeplinks)
    TimelineSource.Adb -> TimelineDestination.Screen(SubScreen.AdbCommander)
}
