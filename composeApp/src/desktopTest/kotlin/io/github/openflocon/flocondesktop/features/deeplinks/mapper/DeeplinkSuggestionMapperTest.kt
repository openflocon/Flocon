package io.github.openflocon.flocondesktop.features.deeplinks.mapper

import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkSuggestionUiModel
import kotlin.test.Test
import kotlin.test.assertEquals

class DeeplinkSuggestionMapperTest {

    private val suggestions = listOf(
        DeeplinkSuggestionUiModel(value = "shoes", label = "Shoes", isDefault = true),
        DeeplinkSuggestionUiModel(value = "shirts", label = "T-shirts & shirts"),
        DeeplinkSuggestionUiModel(value = "accessories"),
    )

    @Test
    fun `a labelled value is displayed with its label`() {
        assertEquals("T-shirts & shirts", suggestions.displayTextOf("shirts"))
    }

    @Test
    fun `a value without label, or unknown, is displayed as is`() {
        assertEquals("accessories", suggestions.displayTextOf("accessories"))
        assertEquals("hats", suggestions.displayTextOf("hats"))
        assertEquals("", suggestions.displayTextOf(""))
        assertEquals("hats", null.displayTextOf("hats"))
    }

    @Test
    fun `typing the label of a suggestion gives its value`() {
        assertEquals("shirts", suggestions.valueForInput("T-shirts & shirts"))
    }

    @Test
    fun `any other typed text stays as it is`() {
        assertEquals("T-shirts", suggestions.valueForInput("T-shirts"))
        assertEquals("accessories", suggestions.valueForInput("accessories"))
        assertEquals("", suggestions.valueForInput(""))
        assertEquals("hats", null.valueForInput("hats"))
    }
}
