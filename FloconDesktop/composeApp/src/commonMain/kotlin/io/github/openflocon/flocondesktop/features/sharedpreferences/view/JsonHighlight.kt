package io.github.openflocon.flocondesktop.features.sharedpreferences.view

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

internal data class JsonHighlightColors(
    val key: Color,
    val string: Color,
    val number: Color,
    val punctuation: Color,
)

/** Colors an already pretty printed json: keys, strings, numbers / literals and punctuation. */
internal fun highlightJson(json: String, colors: JsonHighlightColors): AnnotatedString = buildAnnotatedString {
    var i = 0
    while (i < json.length) {
        val c = json[i]
        when {
            c == '"' -> {
                var end = i + 1
                while (end < json.length && json[end] != '"') {
                    if (json[end] == '\\') end++
                    end++
                }
                end = minOf(end + 1, json.length)
                var next = end
                while (next < json.length && json[next].isWhitespace()) next++
                val isKey = next < json.length && json[next] == ':'
                withStyle(SpanStyle(color = if (isKey) colors.key else colors.string)) { append(json.substring(i, end)) }
                i = end
            }

            c.isDigit() || c == '-' -> {
                var end = i + 1
                while (end < json.length && (json[end].isDigit() || json[end] in ".eE+-")) end++
                withStyle(SpanStyle(color = colors.number)) { append(json.substring(i, end)) }
                i = end
            }

            c.isLetter() -> {
                var end = i
                while (end < json.length && json[end].isLetter()) end++
                withStyle(SpanStyle(color = colors.number)) { append(json.substring(i, end)) }
                i = end
            }

            else -> {
                withStyle(SpanStyle(color = colors.punctuation)) { append(c) }
                i++
            }
        }
    }
}
