package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import io.github.openflocon.flocondesktop.features.database.view.SqlTokenType
import io.github.openflocon.flocondesktop.features.database.view.sqlTokens

/**
 * A query on one line, to keep the row short: the SQL keywords are in bold to find them at a glance.
 */
internal fun sqlTitle(
    query: String,
    keywordColor: Color,
    textColor: Color,
): AnnotatedString = buildAnnotatedString {
    for (token in sqlTokens(query)) {
        if (token.type == SqlTokenType.Keyword) {
            withStyle(SpanStyle(color = keywordColor, fontWeight = FontWeight.Bold)) { append(token.text) }
        } else {
            withStyle(SpanStyle(color = textColor, fontWeight = FontWeight.Normal)) { append(oneLine(token.text)) }
        }
    }
}

// a line break or an indentation becomes one space
private fun oneLine(text: String): String = if (text.isNotEmpty() && text.isBlank()) " " else text.replace('\n', ' ').replace('\r', ' ')
