package io.github.openflocon.mcp.timeline

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timeline.models.TimelineEventRef
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.usecase.GetTimelineEventsUseCase
import io.github.openflocon.domain.timeline.usecase.GetTimelineSummaryUseCase
import io.github.openflocon.mcp.McpTargetResolver
import io.github.openflocon.mcp.McpToolException
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.McpToolServer
import io.github.openflocon.mcp.addTypedTool
import io.github.openflocon.mcp.readOnlyToolAnnotations
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

internal class TimelineMcpToolProvider(
    private val targetResolver: McpTargetResolver,
    private val getTimelineEventsUseCase: GetTimelineEventsUseCase,
    private val getTimelineSummaryUseCase: GetTimelineSummaryUseCase,
    private val zone: ZoneId,
    nowMs: () -> Long,
) : McpToolProvider {

    private val windowResolver = TimelineWindowResolver(zone = zone, nowMs = nowMs)
    private val narrator = TimelineNarrator(zone)

    override fun register(server: McpToolServer) {
        server.addTypedTool(
            name = "get_timeline",
            title = "Get the timeline of the app",
            description = "Tells what the mobile app did in a window of time, above the network: every source (network, database, " +
                "analytics, crashes, deeplinks...) in one narrative, one line per event, the repetitions folded " +
                "(12× GET /feed in 3 s), the anomalies listed first (crash, HTTP errors, calls in a loop, large responses). " +
                "Give a time, or nothing: the tool picks a window (the latest crash, otherwise the last 2 minutes) and says which and why. " +
                "Use it before the list_* tools to understand what happened around a moment (\"before the crash\", \"at 14:32\"), " +
                "or with search to know if an event was sent. " +
                "Each line ends with an id source:id: network:<callId> works with get_network_call, crash:<crashId> with get_crash_report, " +
                "the other sources with their list_* tool. The times are those of the phone.",
            annotations = readOnlyToolAnnotations,
            handler = ::getTimeline,
        )
    }

    private suspend fun getTimeline(input: GetTimelineInput): GetTimelineMcpResult {
        val target = targetResolver.resolve(input)
        val search = searchOf(input)
        val maxLines = input.maxLines.coerceIn(1, MAX_LINES)

        val anchors = anchorsOf(target)
        val eventAnchor = input.eventId?.let { eventAnchorOf(target, it) }
        val window = windowResolver.resolve(input, anchors, eventAnchor)

        val events = if (window.isEmpty) {
            emptyList()
        } else {
            getTimelineEventsUseCase(target, TimelineFilterDomainModel(fromMs = window.fromMs, toMs = window.toMs))
        }
        val narrative = narrator.narrate(events, anchorMs = window.anchorMs, search = search, maxLines = maxLines)

        return GetTimelineMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            window = TimelineWindowMcpModel(
                fromMs = window.fromMs,
                toMs = window.toMs,
                fromLocal = local(window.fromMs),
                toLocal = local(window.toMs),
                timeZone = zone.id,
                reason = window.reason,
            ),
            eventCount = narrative.eventCount,
            anomalyCount = narrative.anomalyCount,
            anomalies = narrative.anomalies,
            lines = narrative.lines,
            found = narrative.found,
            searchMatches = narrative.searchMatches,
            truncated = narrative.truncated,
            omittedCount = narrative.omittedCount,
            notes = buildList {
                add("The times are those of the phone: they can differ from the clock of this computer.")
                if (narrative.truncated) {
                    add("${narrative.omittedCount} lines omitted, the nearest to the anchor of the window are kept: narrow the window (mode at or range) or use search.")
                }
                if (events.count { it.source == TimelineSource.Database } >= DATABASE_LOGS_KEPT) {
                    add("The app keeps only its most recent $DATABASE_LOGS_KEPT database logs: older database events may be missing.")
                }
            },
        )
    }

    // the sessions and the crashes tell where to look, the window is read afterwards
    private suspend fun anchorsOf(target: DeviceIdAndPackageNameDomainModel): TimelineAnchors {
        val markers = getTimelineEventsUseCase(
            target,
            TimelineFilterDomainModel(sources = setOf(TimelineSource.AppStart, TimelineSource.Crash)),
        )
        val summary = getTimelineSummaryUseCase(target, TimelineFilterDomainModel())
        return TimelineAnchors(
            sessionStarts = markers.filter { it.source == TimelineSource.AppStart }.map { it.timestamp }.sorted(),
            crashes = markers.filter { it.source == TimelineSource.Crash }.map {
                TimelineCrash(id = it.id, timestampMs = it.timestamp, title = it.title)
            },
            lastEventMs = summary.sources
                .filterKeys { it != TimelineSource.AppStart }
                .values
                .mapNotNull { it.lastTimestamp }
                .maxOrNull(),
        )
    }

    private suspend fun eventAnchorOf(target: DeviceIdAndPackageNameDomainModel, eventId: String): TimelineEventAnchor {
        val source = TimelineSource.entries.firstOrNull { eventId.substringBefore(':', "").equals(it.name, ignoreCase = true) }
        val id = eventId.substringAfter(':', "")
        if (source == null || id.isEmpty()) {
            throw McpToolException("Unreadable event id '$eventId': expected source:id, like network:<callId>, as in the lines of a previous answer.")
        }
        val event = getTimelineEventsUseCase(target, TimelineFilterDomainModel(events = setOf(TimelineEventRef(source, id)))).firstOrNull()
            ?: throw McpToolException("Event '$eventId' not found. Use the ids of a previous get_timeline answer.")
        return TimelineEventAnchor(
            id = "${source.name.lowercase(Locale.ROOT)}:${event.id}",
            timestampMs = event.timestamp,
            label = event.title.take(LABEL_MAX).ifBlank { source.name },
        )
    }

    private fun searchOf(input: GetTimelineInput): TimelineSearch? {
        val text = input.search?.takeIf { it.isNotBlank() } ?: return null
        val source = input.searchSource?.takeIf { it.isNotBlank() }?.let { name ->
            TimelineSource.entries.firstOrNull { it != TimelineSource.AppStart && it.name.equals(name, ignoreCase = true) }
                ?: throw McpToolException(
                    "Unknown searchSource '$name'. Use one of: " +
                        TimelineSource.entries.filter { it != TimelineSource.AppStart }.joinToString { it.name.lowercase(Locale.ROOT) } + ".",
                )
        }
        return TimelineSearch(text = text, source = source)
    }

    private fun local(ms: Long): String = ZonedDateTime.ofInstant(Instant.ofEpochMilli(ms), zone).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

    private companion object {
        const val LABEL_MAX = 80
    }
}
