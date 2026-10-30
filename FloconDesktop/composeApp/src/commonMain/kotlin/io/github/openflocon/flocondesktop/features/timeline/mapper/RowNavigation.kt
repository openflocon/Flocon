package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.timeline.models.TimelineSource
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
 * and the network calls, which are linked by their request and their response.
 */
internal fun TimelineRowUiModel.linkKey(): String? = when (source) {
    TimelineSource.AppStart, TimelineSource.Network, TimelineSource.Database, TimelineSource.WebView -> null
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
