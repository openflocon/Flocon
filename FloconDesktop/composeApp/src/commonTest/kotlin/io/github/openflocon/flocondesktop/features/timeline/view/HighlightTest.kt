package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HighlightTest {

    private fun marked(text: String, query: String): List<String> {
        val result = highlightMatches(AnnotatedString(text), query, Color.Yellow)
        return result.spanStyles.map { result.text.substring(it.start, it.end) }
    }

    @Test
    fun `every occurrence is marked, whatever the case`() {
        assertEquals(listOf("User", "user"), marked("User and user", "USER"))
    }

    @Test
    fun `a blank filter or no match marks nothing`() {
        assertTrue(marked("users", "").isEmpty())
        assertTrue(marked("users", "  ").isEmpty())
        assertTrue(marked("users", "orders").isEmpty())
    }
}
