package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString

/**
 * [text] with a background behind every occurrence of [query], whatever the case: what the filter matched.
 */
internal fun highlightMatches(
    text: AnnotatedString,
    query: String,
    background: Color,
): AnnotatedString {
    if (query.isBlank()) return text
    val ranges = buildList {
        var from = text.text.indexOf(query, startIndex = 0, ignoreCase = true)
        while (from >= 0) {
            add(from until from + query.length)
            from = text.text.indexOf(query, startIndex = from + query.length, ignoreCase = true)
        }
    }
    if (ranges.isEmpty()) return text
    return buildAnnotatedString {
        append(text)
        ranges.forEach { addStyle(SpanStyle(background = background), it.first, it.last + 1) }
    }
}
