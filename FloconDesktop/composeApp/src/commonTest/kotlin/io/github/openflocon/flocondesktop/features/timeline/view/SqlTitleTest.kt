package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class SqlTitleTest {

    private fun title(query: String) = sqlTitle(query, keywordColor = Color.White, textColor = Color.Gray)

    private fun boldWords(query: String): List<String> {
        val title = title(query)
        return title.spanStyles
            .filter { it.item.fontWeight == FontWeight.Bold }
            .map { title.text.substring(it.start, it.end) }
    }

    @Test
    fun `keywords are bold, whatever their case`() {
        assertEquals(listOf("select", "FROM", "where"), boldWords("select id FROM users where id = 1"))
    }

    @Test
    fun `a keyword inside a string or a name is not bold`() {
        assertEquals(listOf("SELECT", "FROM"), boldWords("SELECT 'from here', selection FROM t"))
    }

    @Test
    fun `the query stays on one line`() {
        val text = title("SELECT *\n  FROM users").text

        assertFalse('\n' in text)
        assertEquals("SELECT * FROM users", text)
    }
}
