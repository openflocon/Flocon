package io.github.openflocon.mcp.timeline

import io.github.openflocon.domain.timeline.models.TimelineEventDomainModel
import io.github.openflocon.domain.timeline.models.TimelinePhase
import io.github.openflocon.domain.timeline.models.TimelineSource
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

// 3 or more identical events following each other, at most this far apart, are told as one line
internal const val FOLD_MIN_COUNT = 3
internal const val FOLD_MAX_GAP_MS = 2_000L

// the same call this many times in this long is a loop
internal const val LOOP_MIN_COUNT = 5
internal const val LOOP_WINDOW_MS = 10_000L

// a loop goes on while the calls keep that pace: LOOP_MIN_COUNT calls in LOOP_WINDOW_MS is one call every 2.5 s at least
internal const val LOOP_CONTINUE_GAP_MS = LOOP_WINDOW_MS / (LOOP_MIN_COUNT - 1)

internal const val LARGE_RESPONSE_BYTES = 1_048_576L

internal const val MAX_ANOMALIES = 20
internal const val MAX_SEARCH_MATCHES = 50

internal data class TimelineSearch(
    val text: String,
    val source: TimelineSource?,
)

internal data class TimelineNarrative(
    val lines: List<String>,
    val anomalies: List<String>,
    val anomalyCount: Int,
    // null when no search was asked
    val found: Boolean?,
    val searchMatches: List<String>,
    val truncated: Boolean,
    val omittedCount: Int,
    val eventCount: Int,
)

/**
 * Tells the events of a window in a few lines: the repetitions are folded, the anomalies are listed apart,
 * and the answer is capped without ever dropping what a search asked for.
 */
internal class TimelineNarrator(zone: ZoneId) {

    private val clock = DateTimeFormatter.ofPattern("HH:mm:ss.SSS").withZone(zone)

    /** An anomaly: the lowest rank is the most serious kind */
    private class Anomaly(val rank: Int, val timeMs: Long, val text: String)

    /** A line of the narrative, with the time of its first event to keep the nearest ones when the answer is capped */
    private class Line(val timeMs: Long, val text: String)

    /** The events told by one line: a single event, or a fold of identical ones */
    private class Item(val events: List<TimelineEventDomainModel>, val key: String?)

    fun narrate(
        eventsNewestFirst: List<TimelineEventDomainModel>,
        anchorMs: Long,
        search: TimelineSearch?,
        maxLines: Int,
    ): TimelineNarrative {
        // a call is told once, by its request: the response events are not lines of their own
        val events = eventsNewestFirst.filter { it.phase != TimelinePhase.Response }.sortedBy { it.timestamp }
        val gaps = events.mapIndexed { index, event -> if (index == 0) 0L else event.timestamp - events[index - 1].timestamp }

        val allAnomalies = anomaliesOf(events)
        val anomalies = cappedAnomalies(allAnomalies)
        val lines = foldedItems(events).let { items ->
            items.mapIndexed { index, item ->
                val previousEnd = if (index == 0) item.events.first().timestamp else items[index - 1].events.last().timestamp
                lineOf(item, gapMs = item.events.first().timestamp - previousEnd)
            }
        }
        val kept = if (lines.size > maxLines) {
            lines.sortedWith(compareBy({ abs(it.timeMs - anchorMs) }, { it.timeMs })).take(maxLines).sortedBy { it.timeMs }
        } else {
            lines
        }

        val matches = search?.let { wanted ->
            events.withIndex()
                .filter { (_, event) -> wanted.source == null || event.source == wanted.source }
                .filter { (_, event) -> event.title.contains(wanted.text, ignoreCase = true) || event.subtitle?.contains(wanted.text, ignoreCase = true) == true }
                .take(MAX_SEARCH_MATCHES)
                .map { (index, event) -> singleLine(event, gapMs = gaps[index]) }
        }

        return TimelineNarrative(
            lines = kept.map { it.text },
            anomalies = anomalies.map { it.text },
            anomalyCount = allAnomalies.size,
            found = matches?.isNotEmpty(),
            searchMatches = matches.orEmpty(),
            truncated = lines.size > kept.size,
            omittedCount = lines.size - kept.size,
            eventCount = events.size,
        )
    }

    // ---- folding

    private fun foldedItems(events: List<TimelineEventDomainModel>): List<Item> {
        val items = ArrayList<Item>()
        var group = ArrayList<TimelineEventDomainModel>()
        var groupKey: String? = null

        fun flush() {
            if (group.size >= FOLD_MIN_COUNT) {
                items.add(Item(group, groupKey))
            } else {
                group.forEach { items.add(Item(listOf(it), null)) }
            }
            group = ArrayList()
        }

        events.forEach { event ->
            val key = foldKey(event)
            val continues = key != null && key == groupKey && event.timestamp - group.last().timestamp <= FOLD_MAX_GAP_MS
            if (!continues) {
                flush()
                groupKey = key
            }
            group.add(event)
        }
        flush()
        return items
    }

    // events with the same key are the same thing happening again; null: never folded (a crash, an app start)
    private fun foldKey(event: TimelineEventDomainModel): String? = when (event.source) {
        TimelineSource.Network -> "net|${networkKey(event)}|${outcomeOf(event)}"
        TimelineSource.Crash, TimelineSource.AppStart -> null
        else -> "${event.source}|${event.title}"
    }

    // an error is never folded with successes, and a code is not another code
    private fun outcomeOf(event: TimelineEventDomainModel): String = when {
        (event.statusCode ?: 0) >= 400 -> "http${event.statusCode}"
        event.hasError -> "failed"
        event.statusCode == null -> "pending"
        else -> "ok"
    }

    private fun networkKey(event: TimelineEventDomainModel): String = "${event.method.orEmpty()} ${hostOf(event.title)}${normalizedPathOf(event.title)}"

    // ---- anomalies

    private fun anomaliesOf(events: List<TimelineEventDomainModel>): List<Anomaly> {
        val found = ArrayList<Anomaly>()
        events.forEach { event ->
            val at = time(event.timestamp)
            val id = idOf(event)
            if (event.source == TimelineSource.Crash) {
                val detail = event.subtitle?.takeIf { it.isNotBlank() }?.let { ": ${it.truncate(80)}" }.orEmpty()
                found.add(Anomaly(0, event.timestamp, "CRASH ${event.title}$detail at $at [$id]"))
            }
            if (event.source == TimelineSource.Network) {
                val call = "${event.method.orEmpty()} ${pathOf(event.title)}".trim()
                val status = event.statusCode
                if (status != null && status >= 400) {
                    found.add(Anomaly(2, event.timestamp, "HTTP $status $call at $at [$id]"))
                } else if (event.hasError) {
                    found.add(Anomaly(2, event.timestamp, "FAILED $call at $at [$id]"))
                }
                val bytes = event.responseByteSize
                if (bytes != null && bytes >= LARGE_RESPONSE_BYTES) {
                    val kind = if (event.isImage) "LARGE image" else "LARGE"
                    found.add(Anomaly(3, event.timestamp, "$kind ${formatBytes(bytes)} response $call at $at [$id]"))
                }
            }
        }
        loopsOf(events).forEach { found.add(Anomaly(1, it.first, it.second)) }
        return found.sortedWith(compareBy({ it.rank }, { it.timeMs }))
    }

    // the most serious kinds first, but a kind is never dropped for the benefit of another: a flood of crashes
    // must not hide the one http error, so the first anomaly of each kind is reserved
    private fun cappedAnomalies(sorted: List<Anomaly>): List<Anomaly> {
        if (sorted.size <= MAX_ANOMALIES) return sorted
        val reserved = sorted.groupBy { it.rank }.values.map { it.first() }
        val rest = sorted.filter { anomaly -> reserved.none { it === anomaly } }.take(MAX_ANOMALIES - reserved.size)
        return (reserved + rest).sortedWith(compareBy({ it.rank }, { it.timeMs }))
    }

    // the same call (method + normalized path) at least LOOP_MIN_COUNT times within LOOP_WINDOW_MS, once per burst
    private fun loopsOf(events: List<TimelineEventDomainModel>): List<Pair<Long, String>> {
        val loops = ArrayList<Pair<Long, String>>()
        events.filter { it.source == TimelineSource.Network }.groupBy { networkKey(it) }.forEach { (_, calls) ->
            var start = 0
            while (start < calls.size) {
                var end = start
                while (end + 1 < calls.size && calls[end + 1].timestamp - calls[start].timestamp <= LOOP_WINDOW_MS) end++
                if (end - start + 1 >= LOOP_MIN_COUNT) {
                    // the same pace goes on: it is the same loop, not one per burst of 10 s
                    while (end + 1 < calls.size && calls[end + 1].timestamp - calls[end].timestamp <= LOOP_CONTINUE_GAP_MS) end++
                    val count = end - start + 1
                    val first = calls[start]
                    val last = calls[end]
                    val call = "${first.method.orEmpty()} ${normalizedPathOf(first.title)}".trim()
                    loops.add(
                        first.timestamp to "LOOP $count× $call in ${formatDuration((last.timestamp - first.timestamp).toDouble())} " +
                            "from ${time(first.timestamp)} [${idOf(first)} … ${idOf(last)}]"
                    )
                    start = end + 1
                } else {
                    start++
                }
            }
        }
        return loops
    }

    // ---- lines

    private fun lineOf(item: Item, gapMs: Long): Line {
        val first = item.events.first()
        if (item.events.size == 1) return singleLineModel(first, gapMs)
        val last = item.events.last()
        val span = formatDuration((last.timestamp - first.timestamp).toDouble())
        val what = when (first.source) {
            TimelineSource.Network -> listOfNotNull(
                "${item.events.size}× ${first.method.orEmpty()} ${normalizedPathOf(first.title)}".trim(),
                statusOf(first),
                // the size of a call is not hidden by the fold
                item.events.mapNotNull { it.responseByteSize }.maxOrNull()?.let { "largest ${formatBytes(it)}" },
            ).joinToString("  ")

            else -> "${item.events.size}× ${first.title.truncate(TITLE_MAX)}"
        }
        return Line(first.timestamp, "${gap(gapMs)}  ${time(first.timestamp)}  ${labelOf(first.source)}  $what in $span  [${idOf(first)} … ${idOf(last)}]")
    }

    private fun singleLine(event: TimelineEventDomainModel, gapMs: Long): String = singleLineModel(event, gapMs).text

    private fun singleLineModel(event: TimelineEventDomainModel, gapMs: Long): Line {
        val what = when (event.source) {
            TimelineSource.Network -> listOfNotNull(
                "${event.method.orEmpty()} ${pathOf(event.title)}".trim(),
                statusOf(event),
                event.durationMs?.let(::formatDuration),
                event.responseByteSize?.let(::formatBytes),
            ).joinToString("  ")

            else -> {
                val subtitle = event.subtitle?.takeIf { it.isNotBlank() }?.let { " (${it.truncate(SUBTITLE_MAX)})" }.orEmpty()
                "${event.title.truncate(TITLE_MAX)}$subtitle"
            }
        }
        return Line(event.timestamp, "${gap(gapMs)}  ${time(event.timestamp)}  ${labelOf(event.source)}  $what  [${idOf(event)}]")
    }

    private fun statusOf(event: TimelineEventDomainModel): String? = when {
        event.statusCode != null -> event.statusCode.toString()
        event.hasError -> "FAILED"
        else -> "pending"
    }

    private fun gap(ms: Long): String = "+${formatDuration(ms.toDouble())}"

    private fun time(ms: Long): String = clock.format(Instant.ofEpochMilli(ms))

    private fun idOf(event: TimelineEventDomainModel): String = "${event.source.name.lowercase(Locale.ROOT)}:${event.id}"

    private fun labelOf(source: TimelineSource): String = when (source) {
        TimelineSource.Network -> "NET"
        TimelineSource.Database -> "DB"
        TimelineSource.Analytics -> "ANA"
        TimelineSource.Accessibility -> "A11Y"
        TimelineSource.WebView -> "WEB"
        TimelineSource.Crash -> "CRASH"
        TimelineSource.Table -> "TABLE"
        TimelineSource.Deeplink -> "LINK"
        TimelineSource.Adb -> "ADB"
        TimelineSource.AppStart -> "START"
    }

    private companion object {
        const val TITLE_MAX = 100
        const val SUBTITLE_MAX = 40

        val UUID = Regex("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")
    }

    // ---- urls and numbers

    private fun hostOf(url: String): String = url.substringAfter("://", "").substringBefore('/').substringBefore('?')

    private fun pathOf(url: String): String {
        val afterScheme = url.substringAfter("://", url)
        val path = if (afterScheme.contains('/')) "/" + afterScheme.substringAfter('/') else afterScheme
        return path.substringBefore('?').substringBefore('#')
    }

    // the numeric and uuid segments are an id: the same call for another id is the same call
    private fun normalizedPathOf(url: String): String = pathOf(url).split('/').joinToString("/") { segment ->
        if (segment.isNotEmpty() && (segment.all { it.isDigit() } || UUID.matches(segment))) ":id" else segment
    }

    private fun String.truncate(max: Int): String = if (length <= max) this else take(max - 1) + "…"

    private fun formatBytes(bytes: Long): String = when {
        bytes < 1_024 -> "$bytes B"
        bytes < 1_048_576 -> String.format(Locale.ROOT, "%.1f KB", bytes / 1_024.0)
        else -> String.format(Locale.ROOT, "%.1f MB", bytes / 1_048_576.0)
    }

    private fun formatDuration(ms: Double): String = when {
        ms < 1_000 -> "${ms.toLong()} ms"
        ms < 60_000 -> String.format(Locale.ROOT, "%.1f s", ms / 1_000)
        else -> String.format(Locale.ROOT, "%.1f min", ms / 60_000)
    }
}
