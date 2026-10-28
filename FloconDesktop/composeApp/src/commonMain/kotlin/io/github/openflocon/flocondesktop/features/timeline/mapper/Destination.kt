package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.models.settings.NetworkDetailTab
import io.github.openflocon.domain.timeline.models.TimelinePhase
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.adbcommander.AdbCommanderRoutes
import io.github.openflocon.flocondesktop.features.analytics.AnalyticsRoutes
import io.github.openflocon.flocondesktop.features.crashreporter.CrashReporterRoutes
import io.github.openflocon.flocondesktop.features.database.DatabaseRoutes
import io.github.openflocon.flocondesktop.features.deeplinks.DeeplinkRoutes
import io.github.openflocon.flocondesktop.features.network.NetworkRoutes
import io.github.openflocon.flocondesktop.features.table.TableRoutes
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRowUiModel
import io.github.openflocon.navigation.FloconRoute

internal sealed interface TimelineDestination {
    // the detail of the event, displayed in a panel next to the timeline
    data class Panel(val route: FloconRoute) : TimelineDestination
}

private fun TimelinePhase.toNetworkTab(): NetworkDetailTab = when (this) {
    TimelinePhase.Request -> NetworkDetailTab.Request
    TimelinePhase.Response -> NetworkDetailTab.Response
}

// null: nothing to open for this event. The request line of a call opens its request tab, the response line its response tab
internal fun TimelineRowUiModel.toDestination(): TimelineDestination? = when (source) {
    TimelineSource.AppStart -> null
    TimelineSource.Network -> TimelineDestination.Panel(NetworkRoutes.Panel(requestId = id, tab = phase.toNetworkTab()))
    TimelineSource.Analytics -> TimelineDestination.Panel(AnalyticsRoutes.Detail(id = id))
    TimelineSource.Crash -> TimelineDestination.Panel(CrashReporterRoutes.Panel(crashId = id))
    TimelineSource.Database -> id.toIntOrNull()?.let { TimelineDestination.Panel(DatabaseRoutes.QueryLogPanel(id = it)) }
    TimelineSource.Table -> TimelineDestination.Panel(TableRoutes.Detail(id = id))
    TimelineSource.Deeplink -> id.toLongOrNull()?.let { TimelineDestination.Panel(DeeplinkRoutes.Panel(id = it)) }
    TimelineSource.Adb -> id.toLongOrNull()?.let { TimelineDestination.Panel(AdbCommanderRoutes.Panel(id = it)) }
}
