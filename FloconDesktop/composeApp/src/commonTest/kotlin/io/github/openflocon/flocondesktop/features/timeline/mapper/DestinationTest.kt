package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.app.ui.model.SubScreen
import io.github.openflocon.flocondesktop.features.adbcommander.AdbCommanderRoutes
import io.github.openflocon.flocondesktop.features.analytics.AnalyticsRoutes
import io.github.openflocon.flocondesktop.features.crashreporter.CrashReporterRoutes
import io.github.openflocon.flocondesktop.features.database.DatabaseRoutes
import io.github.openflocon.flocondesktop.features.deeplinks.DeeplinkRoutes
import io.github.openflocon.flocondesktop.features.network.NetworkRoutes
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRowUiModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DestinationTest {

    private fun row(source: TimelineSource, id: String = "42") = TimelineRowUiModel(
        id = id,
        source = source,
        day = "20/10/2026",
        time = "12:00:00.000",
        timestamp = 0L,
        title = "title",
        subtitle = null,
    )

    @Test
    fun `a network call opens its detail panel`() {
        assertEquals(
            TimelineDestination.Panel(NetworkRoutes.Panel(requestId = "call-1")),
            row(TimelineSource.Network, id = "call-1").toDestination(),
        )
    }

    @Test
    fun `an analytics event opens its detail panel`() {
        assertEquals(
            TimelineDestination.Panel(AnalyticsRoutes.Detail(id = "item-1")),
            row(TimelineSource.Analytics, id = "item-1").toDestination(),
        )
    }

    @Test
    fun `a crash opens its detail panel`() {
        assertEquals(
            TimelineDestination.Panel(CrashReporterRoutes.Panel(crashId = "crash-1")),
            row(TimelineSource.Crash, id = "crash-1").toDestination(),
        )
    }

    @Test
    fun `an adb command opens its detail panel`() {
        assertEquals(
            TimelineDestination.Panel(AdbCommanderRoutes.Panel(id = 7L)),
            row(TimelineSource.Adb, id = "7").toDestination(),
        )
    }

    @Test
    fun `a deeplink opens its detail panel`() {
        assertEquals(
            TimelineDestination.Panel(DeeplinkRoutes.Panel(id = 9L)),
            row(TimelineSource.Deeplink, id = "9").toDestination(),
        )
    }

    @Test
    fun `a database query opens its detail panel`() {
        assertEquals(
            TimelineDestination.Panel(DatabaseRoutes.QueryLogPanel(id = 5)),
            row(TimelineSource.Database, id = "5").toDestination(),
        )
    }

    @Test
    fun `the plugins without detail open their screen`() {
        assertEquals(TimelineDestination.Screen(SubScreen.Tables), row(TimelineSource.Table).toDestination())
    }

    @Test
    fun `an app start has nothing to open`() {
        assertNull(row(TimelineSource.AppStart).toDestination())
    }

    @Test
    fun `every other source has a destination`() {
        (TimelineSource.entries - TimelineSource.AppStart).forEach {
            assertNotNull(row(it).toDestination(), "$it should open something")
        }
    }
}
