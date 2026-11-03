package io.github.openflocon.mcp.timeline

import io.github.openflocon.mcp.McpToolException
import java.time.Instant
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

// the window around a crash: what led to it, and the few seconds after
internal const val BEFORE_CRASH_MS = 60_000L
internal const val AFTER_CRASH_MS = 5_000L

// the crash kills the session and the restart creates a new one: a crash this close before a session still belongs to the previous one
internal const val CRASH_BEFORE_SESSION_MS = 60_000L

internal const val DEFAULT_WINDOW_MS = 120_000L

internal data class TimelineCrash(
    val id: String,
    val timestampMs: Long,
    val title: String,
)

/** What the timeline knows about the app, to choose a window: the events themselves are read afterwards */
internal data class TimelineAnchors(
    val sessionStarts: List<Long>,
    val crashes: List<TimelineCrash>,
    // times are those of the phone: the window is measured from its last event, never from the clock of the computer
    val lastEventMs: Long?,
)

internal data class TimelineEventAnchor(
    val id: String,
    val timestampMs: Long,
    val label: String,
)

internal data class TimelineWindow(
    val fromMs: Long,
    val toMs: Long,
    val reason: String,
    val anchorEventId: String?,
)

internal class TimelineWindowResolver(
    private val zone: ZoneId,
    private val nowMs: () -> Long,
) {
    private val clock = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(zone)

    fun resolve(input: GetTimelineInput, anchors: TimelineAnchors, event: TimelineEventAnchor?): TimelineWindow {
        if (input.eventId != null && event == null) {
            throw McpToolException("Event '${input.eventId}' not found. Use the ids of a previous get_timeline answer.")
        }
        val mode = input.mode ?: when {
            input.from != null || input.to != null -> TimelineMode.Range
            input.at != null -> TimelineMode.At
            else -> null
        }
        return when {
            event != null && mode == null -> aroundEvent(event, input)
            mode == null -> defaultWindow(anchors)
            mode == TimelineMode.Recent -> recent(input, anchors)
            mode == TimelineMode.Session -> session(anchors)
            mode == TimelineMode.BeforeCrash -> beforeCrash(anchors)
            mode == TimelineMode.At -> at(input, anchors)
            else -> range(input, anchors)
        }
    }

    private fun defaultWindow(anchors: TimelineAnchors): TimelineWindow {
        val latestSession = anchors.sessionStarts.maxOrNull()
        val crash = if (latestSession == null) {
            anchors.crashes.maxByOrNull { it.timestampMs }
        } else {
            anchors.crashes.filter { it.timestampMs >= latestSession }.maxByOrNull { it.timestampMs }
                ?: anchors.crashes
                    .filter { it.timestampMs < latestSession && latestSession - it.timestampMs <= CRASH_BEFORE_SESSION_MS }
                    .maxByOrNull { it.timestampMs }
        }
        if (crash != null) return aroundCrash(crash, prefix = "No time given: anchored on")

        val last = anchors.lastEventMs ?: return noEvents()
        val from = maxOf(last - DEFAULT_WINDOW_MS, latestSession ?: 0L)
        return TimelineWindow(
            fromMs = from,
            toMs = last,
            reason = "No time given and no crash: the last 2 minutes of the latest session (until ${time(last)}).",
            anchorEventId = null,
        )
    }

    private fun recent(input: GetTimelineInput, anchors: TimelineAnchors): TimelineWindow {
        val last = anchors.lastEventMs ?: return noEvents()
        val minutes = input.minutes.coerceIn(1, MAX_RECENT_MINUTES)
        return TimelineWindow(
            fromMs = last - minutes * 60_000L,
            toMs = last,
            reason = "The last $minutes minutes before the last event (${time(last)}).",
            anchorEventId = null,
        )
    }

    private fun session(anchors: TimelineAnchors): TimelineWindow {
        val last = anchors.lastEventMs ?: return noEvents()
        val start = anchors.sessionStarts.maxOrNull()
        return TimelineWindow(
            fromMs = start ?: 0L,
            toMs = last,
            reason = if (start == null) {
                "No session known: every event until ${time(last)}."
            } else {
                "The latest session, from ${time(start)} to ${time(last)}."
            },
            anchorEventId = null,
        )
    }

    private fun beforeCrash(anchors: TimelineAnchors): TimelineWindow {
        val crash = anchors.crashes.maxByOrNull { it.timestampMs }
            ?: throw McpToolException("No crash recorded for this app: nothing to anchor before_crash on.")
        return aroundCrash(crash, prefix = "Anchored on")
    }

    private fun aroundCrash(crash: TimelineCrash, prefix: String) = TimelineWindow(
        fromMs = crash.timestampMs - BEFORE_CRASH_MS,
        toMs = crash.timestampMs + AFTER_CRASH_MS,
        reason = "$prefix the crash ${crash.title} at ${time(crash.timestampMs)}: the minute before it and the 5 seconds after.",
        anchorEventId = "crash:${crash.id}",
    )

    private fun at(input: GetTimelineInput, anchors: TimelineAnchors): TimelineWindow {
        val text = input.at ?: throw McpToolException("The mode at needs the time `at`. ${TIME_FORMATS}")
        val instant = parseTime(text)
        val around = input.aroundSeconds.coerceIn(1, MAX_AROUND_SECONDS) * 1_000L
        val firstSession = anchors.sessionStarts.minOrNull()
        val gap = if (firstSession != null && instant < firstSession) " It is before the first known session." else ""
        return TimelineWindow(
            fromMs = instant - around,
            toMs = instant + around,
            reason = "Around ${time(instant)}, ${around / 1_000} seconds on each side.$gap",
            anchorEventId = null,
        )
    }

    private fun range(input: GetTimelineInput, anchors: TimelineAnchors): TimelineWindow {
        val fromText = input.from ?: throw McpToolException("The mode range needs the start `from`. ${TIME_FORMATS}")
        val from = parseTime(fromText)
        val to = input.to?.let(::parseTime) ?: anchors.lastEventMs ?: nowMs()
        if (to < from) throw McpToolException("`to` (${time(to)}) is before `from` (${time(from)}).")
        return TimelineWindow(
            fromMs = from,
            toMs = to,
            reason = "From ${time(from)} to ${time(to)}.",
            anchorEventId = null,
        )
    }

    private fun aroundEvent(event: TimelineEventAnchor, input: GetTimelineInput): TimelineWindow {
        val around = input.aroundSeconds.coerceIn(1, MAX_AROUND_SECONDS) * 1_000L
        return TimelineWindow(
            fromMs = event.timestampMs - around,
            toMs = event.timestampMs + around,
            reason = "Around ${event.label} at ${time(event.timestampMs)}, ${around / 1_000} seconds on each side.",
            anchorEventId = event.id,
        )
    }

    private fun noEvents() = TimelineWindow(
        fromMs = 0L,
        toMs = 0L,
        reason = "No event recorded for this app yet.",
        anchorEventId = null,
    )

    private fun time(ms: Long): String = clock.format(Instant.ofEpochMilli(ms))

    /** Epoch milliseconds, an ISO-8601 instant with an offset, or HH:mm[:ss] (the latest past occurrence of that time) */
    private fun parseTime(text: String): Long {
        val value = text.trim()
        if (value.isNotEmpty() && value.all { it.isDigit() }) return value.toLong()
        if (value.contains('T')) {
            try {
                return OffsetDateTime.parse(value).toInstant().toEpochMilli()
            } catch (_: DateTimeParseException) {
                throw McpToolException("Unreadable time '$text'. $TIME_FORMATS")
            }
        }
        val match = CLOCK_TIME.matchEntire(value) ?: throw McpToolException("Unreadable time '$text'. $TIME_FORMATS")
        val (hour, minute, second) = match.destructured
        val local = try {
            LocalTime.of(hour.toInt(), minute.toInt(), second.ifEmpty { "0" }.toInt())
        } catch (_: java.time.DateTimeException) {
            throw McpToolException("Unreadable time '$text'. $TIME_FORMATS")
        }
        val now = Instant.ofEpochMilli(nowMs()).atZone(zone)
        var candidate = now.toLocalDate().atTime(local).atZone(zone)
        // a time that has not happened yet today was yesterday's
        if (candidate.toInstant().toEpochMilli() > nowMs()) candidate = candidate.minusDays(1)
        return candidate.toInstant().toEpochMilli()
    }

    private companion object {
        val CLOCK_TIME = Regex("""(\d{1,2}):(\d{2})(?::(\d{2}))?""")
        const val TIME_FORMATS = "Accepted: epoch milliseconds, an ISO-8601 instant with an offset (2026-11-03T14:32:00+01:00), or HH:mm[:ss]."
        const val MAX_AROUND_SECONDS = 3_600
        const val MAX_RECENT_MINUTES = 1_440
    }
}
