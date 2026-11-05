package io.github.openflocon.flocondesktop.features.timeline.view

import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.network.options.NetworkItemOption
import io.github.openflocon.flocondesktop.features.network.options.NetworkItemOptionLabels
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineExcludeUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineNetworkUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.previewTimelineRowUiModel
import io.github.openflocon.library.designsystem.common.FloconContextMenuItem
import kotlinx.collections.immutable.persistentListOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class TimelineRowMenuTest {

    private val networkLabels = NetworkItemOptionLabels(
        copyUrl = "Copy URL",
        copyCurl = "Copy cURL",
        createMock = "Create mock",
        replay = "Replay",
        shareAsMarkdown = "Share",
    )

    private val labels = TimelineRowMenuLabels(
        exclude = "Exclude requests",
        excludeBy = "Exclude by",
        manageExclusions = "Manage exclusions…",
        source = "Source",
        sourceName = "Network",
        path = "Path",
        captions = mapOf(
            TimelineFacet.NetworkMethod to "Method",
            TimelineFacet.NetworkHost to "Host",
            TimelineFacet.NetworkStatus to "Status",
            TimelineFacet.DatabaseName to "Database",
        ),
        copyUrlShortcut = "Ctrl ⇧ C",
    )

    private val excluded = mutableListOf<String>()

    private val callbacks = TimelineRowMenuCallbacks(
        onNetworkOption = { excluded += "option:${it.name}" },
        onExclude = { facet, value -> excluded += "${facet.name}:$value" },
        onExcludePath = { excluded += "path:$it" },
        onExcludeSource = { excluded += "source" },
        onManageExclusions = { excluded += "manage" },
    )

    private fun call(httpActions: Boolean) = previewTimelineRowUiModel(
        source = TimelineSource.Network,
        network = TimelineNetworkUiModel(
            method = if (httpActions) "GET" else "WS",
            host = "ws.postman-echo.com",
            path = "/raw?id=2",
            statusCode = 101,
            duration = null,
            httpActions = httpActions,
        ),
        excludes = persistentListOf(
            TimelineExcludeUiModel(TimelineFacet.NetworkMethod, if (httpActions) "GET" else "WS"),
            TimelineExcludeUiModel(TimelineFacet.NetworkStatus, "1xx"),
            TimelineExcludeUiModel(TimelineFacet.NetworkHost, "ws.postman-echo.com"),
        ),
    )

    @Test
    fun `an http call keeps its network options and gets the exclude submenu after them`() {
        val items = timelineRowMenu(call(httpActions = true), networkLabels, labels, callbacks)

        val options = items.takeWhile { it is FloconContextMenuItem.Item }.map { it.label }
        assertEquals(listOf("Copy URL", "Copy cURL", "Create mock", "Replay", "Share"), options)
        assertIs<FloconContextMenuItem.Separator>(items[options.size])
        val exclude = items.last()
        assertIs<FloconContextMenuItem.SubMenu>(exclude)
        assertEquals("Exclude requests", exclude.label)
    }

    @Test
    fun `only the url of a websocket call can be copied, and the shortcut is shown on it`() {
        val items = timelineRowMenu(call(httpActions = false), networkLabels, labels, callbacks)

        val copy = items.first()
        assertIs<FloconContextMenuItem.Item>(copy)
        assertEquals("Copy URL", copy.label)
        assertEquals("Ctrl ⇧ C", copy.shortcut)
        assertEquals(1, items.filterIsInstance<FloconContextMenuItem.Item>().size)
        copy.onClick()
        assertEquals(listOf("option:${NetworkItemOption.CopyUrl.name}"), excluded)
    }

    @Test
    fun `the submenu of a call lists the method, the host, the path and the source, like the mockup`() {
        val submenu = timelineRowMenu(call(httpActions = false), networkLabels, labels, callbacks).last()
        assertIs<FloconContextMenuItem.SubMenu>(submenu)

        val lines = submenu.items
        assertIs<FloconContextMenuItem.Header>(lines.first())
        assertEquals("Exclude by", lines.first().label)
        // the status is in the filter panel, not in the menu
        assertEquals(listOf("Method", "Host", "Path", "Source"), lines.filterIsInstance<FloconContextMenuItem.Detail>().map { it.label })
        assertIs<FloconContextMenuItem.Separator>(lines[lines.size - 2])
        assertEquals("Manage exclusions…", lines.last().label)

        lines.filterIsInstance<FloconContextMenuItem.Detail>().forEach { it.onClick() }
        lines.last().onClick()
        assertEquals(listOf("NetworkMethod:WS", "NetworkHost:ws.postman-echo.com", "path:/raw", "source", "manage"), excluded)
    }

    @Test
    fun `an event that is not a network call only has the exclude submenu`() {
        val log = previewTimelineRowUiModel(
            source = TimelineSource.Database,
            excludes = persistentListOf(TimelineExcludeUiModel(TimelineFacet.DatabaseName, "app.db")),
        )

        val items = timelineRowMenu(log, networkLabels, labels, callbacks)

        assertEquals(1, items.size)
        val submenu = items.single()
        assertIs<FloconContextMenuItem.SubMenu>(submenu)
        assertTrue(submenu.items.filterIsInstance<FloconContextMenuItem.Detail>().map { it.label } == listOf("Database", "Source"))
    }
}
