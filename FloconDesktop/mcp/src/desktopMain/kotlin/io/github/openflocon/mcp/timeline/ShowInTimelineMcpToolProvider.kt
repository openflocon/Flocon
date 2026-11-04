package io.github.openflocon.mcp.timeline

import io.github.openflocon.domain.screen.models.FloconScreen
import io.github.openflocon.domain.screen.usecase.RequestDesktopScreenUseCase
import io.github.openflocon.domain.timeline.models.TimelineEventRef
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.models.TimelineHiddenReason
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.usecase.GetTimelineEventHiddenReasonsUseCase
import io.github.openflocon.domain.timeline.usecase.GetTimelineEventsUseCase
import io.github.openflocon.domain.timeline.usecase.RequestTimelineFocusUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelineGraphUseCase
import io.github.openflocon.mcp.McpTargetResolver
import io.github.openflocon.mcp.McpToolException
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.McpToolServer
import io.github.openflocon.mcp.addTypedTool
import io.github.openflocon.mcp.screen.McpSelectionApplier
import io.github.openflocon.mcp.writeToolAnnotations
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// the sources the graph of the timeline draws (the lanes of GraphLane), the other ones only appear in the list
private val GRAPH_SOURCES = setOf(TimelineSource.Network, TimelineSource.Database, TimelineSource.Analytics)

internal class ShowInTimelineMcpToolProvider(
    private val targetResolver: McpTargetResolver,
    private val windowFinder: TimelineWindowFinder,
    private val getTimelineEventsUseCase: GetTimelineEventsUseCase,
    private val selectionApplier: McpSelectionApplier,
    private val setTimelineGraphUseCase: SetTimelineGraphUseCase,
    private val requestTimelineFocusUseCase: RequestTimelineFocusUseCase,
    private val requestDesktopScreenUseCase: RequestDesktopScreenUseCase,
    private val getTimelineEventHiddenReasonsUseCase: GetTimelineEventHiddenReasonsUseCase,
    private val zone: ZoneId,
) : McpToolProvider {

    private val narrator = TimelineNarrator(zone)

    override fun register(server: McpToolServer) {
        server.addTypedTool(
            name = "show_in_timeline",
            title = "Show a zone in the timeline of the Flocon desktop app",
            description = "Moves the Flocon Desktop window for the user: opens the timeline in graph mode (saved in their preferences), " +
                "selects the device and app of the call, selects an event and centers the graph on its zone. " +
                "Give an event id of get_timeline (source:id), a time, or nothing: the latest crash, otherwise the main anomaly of the last minutes. " +
                "Flocon is not brought to the front and the request is only registered: tell the user to go to the Flocon desktop app to see it. " +
                "Nothing is changed when the event or the window is not found. The filters of the user are never changed: " +
                "if one hides the event, the answer says which, tell the user.",
            annotations = writeToolAnnotations(destructive = false, idempotent = true),
            handler = ::show,
        )
    }

    private suspend fun show(input: ShowInTimelineInput): ShowInTimelineMcpResult {
        // everything is checked before anything is changed
        val graph = when (input.view.trim().lowercase(Locale.ROOT)) {
            SHOW_VIEW_GRAPH -> true
            SHOW_VIEW_LIST -> false
            else -> throw McpToolException("Unknown view '${input.view}'. Use '$SHOW_VIEW_GRAPH' or '$SHOW_VIEW_LIST'.")
        }
        val target = targetResolver.resolve(input)
        val found = windowFinder.find(
            target,
            GetTimelineInput(
                mode = input.mode,
                at = input.at,
                from = input.from,
                to = input.to,
                eventId = input.eventId,
                aroundSeconds = input.aroundSeconds,
            ),
        )
        val window = found.window
        if (window.isEmpty) throw McpToolException("No event recorded for this app yet: nothing to show.")
        val events = getTimelineEventsUseCase(target, TimelineFilterDomainModel(fromMs = window.fromMs, toMs = window.toMs))
        if (events.isEmpty()) {
            throw McpToolException(
                "No event between ${local(window.fromMs)} and ${local(window.toMs)}: nothing to show. Use get_timeline to find a window with events.",
            )
        }

        // the event to select: the one asked, the crash of the default window, otherwise the main anomaly of the window
        val event = (window.anchorEventId ?: narrator.narrate(events, window.anchorMs, search = null, maxLines = 1).mainAnomalyId)
            ?.let(::eventRefOf)
        val eventTimeMs = event?.let { ref -> events.firstOrNull { it.source == ref.source && it.id == ref.id }?.timestamp } ?: window.anchorMs
        val sessionStart = found.anchors.sessionStarts.filter { it <= eventTimeMs }.maxOrNull()
        val hiddenReasons = event?.let { getTimelineEventHiddenReasonsUseCase(target, it) }.orEmpty()

        val current = selectionApplier.current()
        val applied = buildList {
            addAll(selectionApplier.select(target, current, selectApp = current?.deviceId != target.deviceId || current.packageName != target.packageName))
            setTimelineGraphUseCase(graph)
            add("Set the timeline view to ${if (graph) SHOW_VIEW_GRAPH else SHOW_VIEW_LIST}")
            requestTimelineFocusUseCase(target, sessionStart, event, window.fromMs, window.toMs)
            add("Requested the zone ${local(window.fromMs)} to ${local(window.toMs)}${event?.let { " with the event ${idOf(it)}" }.orEmpty()}")
            requestDesktopScreenUseCase(FloconScreen.Timeline)
            add("Requested the screen timeline")
        }
        val hiddenBy = hiddenReasons.map { it.describe() }

        return ShowInTimelineMcpResult(
            applied = applied,
            window = TimelineWindowMcpModel(
                fromMs = window.fromMs,
                toMs = window.toMs,
                fromLocal = local(window.fromMs),
                toLocal = local(window.toMs),
                timeZone = zone.id,
                reason = window.reason,
            ),
            eventId = event?.let(::idOf),
            hiddenBy = hiddenBy,
            message = message(graph = graph, window = window, event = event, hiddenBy = hiddenBy),
        )
    }

    private fun message(graph: Boolean, window: TimelineWindow, event: TimelineEventRef?, hiddenBy: List<String>): String = buildString {
        append("Done. Flocon is not brought to the front: ask the user to go to the Flocon desktop app. ")
        append("The request is registered, Flocon applies it when the timeline is displayed. ")
        append("The timeline is shown in ${if (graph) "graph" else "list"} mode: ${window.reason} ")
        if (event == null) append("No anomaly in this zone: only the zone is shown. ")
        if (!graph) append("In list mode the row of the event is selected but the list does not scroll to it. ")
        if (graph && event != null && event.source !in GRAPH_SOURCES) {
            append("The graph does not draw ${event.source.name.lowercase(Locale.ROOT)} events (only network, database and analytics): the zone around it is shown, use view list to see its row. ")
        }
        if (hiddenBy.isNotEmpty()) {
            append("The event exists but the filters of the user hide it (${hiddenBy.joinToString("; ")}). The filters were not changed: tell the user.")
        }
    }.trim()

    private fun TimelineHiddenReason.describe(): String = when (this) {
        TimelineHiddenReason.Source -> "its source is unchecked in the filters"
        TimelineHiddenReason.Text -> "the text search does not match it"
        TimelineHiddenReason.Facets -> "a value is hidden in the filter panel (method, status, database...)"
        TimelineHiddenReason.NetworkRules -> "the network rules (status, host, duration) hide it"
        TimelineHiddenReason.CurrentSessionOnly -> "the filter current session only hides the older sessions"
        TimelineHiddenReason.Combination -> "several filters together hide it"
    }

    // source:id, as in the lines of get_timeline
    private fun eventRefOf(id: String): TimelineEventRef? {
        val source = TimelineSource.entries.firstOrNull { id.substringBefore(':', "").equals(it.name, ignoreCase = true) }
        val eventId = id.substringAfter(':', "")
        return if (source == null || eventId.isEmpty()) null else TimelineEventRef(source, eventId)
    }

    private fun idOf(ref: TimelineEventRef) = "${ref.source.name.lowercase(Locale.ROOT)}:${ref.id}"

    private fun local(ms: Long): String = ZonedDateTime.ofInstant(Instant.ofEpochMilli(ms), zone).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
}
