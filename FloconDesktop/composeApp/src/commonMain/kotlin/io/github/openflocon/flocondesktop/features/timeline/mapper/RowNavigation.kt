package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timelinelog.models.TIMELINE_LOG_TYPE_NAVIGATION
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineItemUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRowUiModel

/**
 * The index of the call to select when moving the selection by [step] (1 or -1) in a list of [count] items.
 * [keyAt] gives the call key of an item that can be opened, null for the others (headers, app start, not loaded yet),
 * which are skipped, as well as the other line of the selected call.
 * Without selection, or when it is not in the list anymore, the first call from the start of the list is the one.
 * Null when there is nothing to move to: the selection stays.
 */
internal fun adjacentCallIndex(
    count: Int,
    selectedKey: String?,
    step: Int,
    keyAt: (Int) -> String?,
): Int? {
    val selectedIndex = selectedKey?.let { key -> (0 until count).firstOrNull { keyAt(it) == key } }
    val (start, direction) = if (selectedIndex == null) 0 to 1 else selectedIndex + step to step
    var index = start
    while (index in 0 until count) {
        val key = keyAt(index)
        if (key != null && key != selectedKey) return index
        index += direction
    }
    return null
}

/**
 * What the copy button of an event puts in the clipboard: the url, the query, the message of the crash...
 * Null when there is nothing to copy.
 */
internal fun TimelineRowUiModel.copyText(): String? = when (source) {
    TimelineSource.AppStart -> null
    TimelineSource.Crash -> listOfNotNull(title, subtitle).joinToString(": ")
    else -> title
}.takeIf { !it.isNullOrBlank() }

/**
 * Events that are the same thing happening again (the same analytics event, the same deeplink...) share a link key:
 * they are linked when one is hovered. Null for the events with nothing to link: the app start, the database queries
 * (a query is often repeated, linking them would only add noise), the WebView events (same for a console message),
 * the accessibility events (the same element is focused or clicked again and again), the logs (the same message is
 * logged again and again), and the network calls, which
 * are linked by their request and their response.
 */
internal fun TimelineRowUiModel.linkKey(): String? = when (source) {
    TimelineSource.AppStart,
    TimelineSource.Network,
    TimelineSource.Database,
    TimelineSource.WebView,
    TimelineSource.Accessibility,
    TimelineSource.Log,
    -> null

    else -> "${source.name}:$title".takeIf { title.isNotBlank() }
}

/**
 * The indices of the rows linked to the hovered one: the two lines of a network call ([callKey]), or the same event
 * repeated ([linkKey]). Nothing without a key: the rows with nothing to link do not match each other.
 */
internal fun linkedRowIndices(
    count: Int,
    callKey: String?,
    linkKey: String?,
    rowAt: (Int) -> TimelineRowUiModel?,
): List<Int> {
    if (callKey == null && linkKey == null) return emptyList()
    return (0 until count).filter { index ->
        val row = rowAt(index) ?: return@filter false
        if (callKey != null) row.callKey == callKey else row.linkKey() == linkKey
    }
}

/**
 * The indices linked by a hovered screenshot: the screenshot itself, then the last event it covers.
 * A screenshot covers the events taken after it, up to the next screenshot. The list goes from the newest, so those are
 * the items above [shotIndex], until the next screenshot or the top of the list. The headers are skipped.
 * Only the screenshot when it covers no event.
 */
internal fun screenshotSpanIndices(
    shotIndex: Int,
    itemAt: (Int) -> TimelineItemUiModel?,
): List<Int> {
    var end: Int? = null
    var index = shotIndex - 1
    while (index >= 0) {
        when (itemAt(index)) {
            is TimelineItemUiModel.Screenshot -> break
            is TimelineItemUiModel.Row -> end = index
            is TimelineItemUiModel.DayHeader, null -> Unit
        }
        index--
    }
    return listOfNotNull(shotIndex, end)
}

private const val NavigationLookBack = 300

/**
 * The name of the screen of a screenshot: the title of the last navigation event before it, so the first one below it as
 * the list goes from the newest. Null when there is none in the items already loaded, [NavigationLookBack] at most.
 */
internal fun screenshotScreenName(
    shotIndex: Int,
    count: Int,
    itemAt: (Int) -> TimelineItemUiModel?,
): String? {
    for (index in shotIndex + 1..minOf(shotIndex + NavigationLookBack, count - 1)) {
        val row = (itemAt(index) as? TimelineItemUiModel.Row)?.row ?: continue
        if (row.source == TimelineSource.Log && row.subtitle.equals(TIMELINE_LOG_TYPE_NAVIGATION, ignoreCase = true) && row.title.isNotBlank()) {
            return row.title
        }
    }
    return null
}
