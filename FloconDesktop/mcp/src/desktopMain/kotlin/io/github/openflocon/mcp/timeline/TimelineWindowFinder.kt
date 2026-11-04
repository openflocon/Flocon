package io.github.openflocon.mcp.timeline

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timeline.models.TimelineEventRef
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.models.TimelineSummaryDomainModel
import io.github.openflocon.domain.timeline.usecase.GetTimelineEventsUseCase
import io.github.openflocon.domain.timeline.usecase.GetTimelineSummaryUseCase
import io.github.openflocon.mcp.McpToolException
import java.time.ZoneId
import java.util.Locale

/**
 * Finds the window of the timeline a tool is about: the sessions and the crashes of the app tell where to look,
 * then the [TimelineWindowResolver] chooses. Shared by the tools that read the timeline and the ones that show it.
 */
internal class TimelineWindowFinder(
    private val getTimelineEventsUseCase: GetTimelineEventsUseCase,
    private val getTimelineSummaryUseCase: GetTimelineSummaryUseCase,
    zone: ZoneId,
    nowMs: () -> Long,
) {
    private val windowResolver = TimelineWindowResolver(zone = zone, nowMs = nowMs)

    internal data class Found(
        val summary: TimelineSummaryDomainModel,
        val anchors: TimelineAnchors,
        // the event of the input eventId, null when none was given
        val eventAnchor: TimelineEventAnchor?,
        val window: TimelineWindow,
    )

    suspend fun find(target: DeviceIdAndPackageNameDomainModel, input: GetTimelineInput): Found {
        val summary = getTimelineSummaryUseCase(target, TimelineFilterDomainModel())
        val anchors = anchorsOf(target, summary)
        val eventAnchor = input.eventId?.let { eventAnchorOf(target, it) }
        return Found(summary, anchors, eventAnchor, windowResolver.resolve(input, anchors, eventAnchor))
    }

    // the sessions and the crashes tell where to look, the window is read afterwards
    private suspend fun anchorsOf(target: DeviceIdAndPackageNameDomainModel, summary: TimelineSummaryDomainModel): TimelineAnchors {
        val markers = getTimelineEventsUseCase(
            target,
            TimelineFilterDomainModel(sources = setOf(TimelineSource.AppStart, TimelineSource.Crash)),
        )
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

    private companion object {
        const val LABEL_MAX = 80
    }
}
