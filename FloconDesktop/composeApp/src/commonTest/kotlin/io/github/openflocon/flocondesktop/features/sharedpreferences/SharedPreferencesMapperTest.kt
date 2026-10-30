package io.github.openflocon.flocondesktop.features.sharedpreferences

import io.github.openflocon.domain.sharedpreference.models.SharedPreferenceRowDomainModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.PreferenceValueTypeUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.autoUpdateDelayPresets
import io.github.openflocon.flocondesktop.features.sharedpreferences.view.PreferenceAutoUpdate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SharedPreferencesMapperTest {

    private fun row(value: SharedPreferenceRowDomainModel.Value) = SharedPreferenceRowDomainModel("k", value).toUi()

    private fun string(value: String) = row(SharedPreferenceRowDomainModel.Value.StringValue(value))

    @Test
    fun `primitive types are detected`() {
        assertEquals(PreferenceValueTypeUiModel.Int, row(SharedPreferenceRowDomainModel.Value.IntValue(1)).type)
        assertEquals(PreferenceValueTypeUiModel.Long, row(SharedPreferenceRowDomainModel.Value.LongValue(1L)).type)
        assertEquals(PreferenceValueTypeUiModel.Float, row(SharedPreferenceRowDomainModel.Value.FloatValue(1f)).type)
        assertEquals(PreferenceValueTypeUiModel.Bool, row(SharedPreferenceRowDomainModel.Value.BooleanValue(true)).type)
        assertEquals(PreferenceValueTypeUiModel.Set, row(SharedPreferenceRowDomainModel.Value.StringSetValue(setOf("a"))).type)
    }

    @Test
    fun `short string is a STR, not collapsible`() {
        val row = string("dark")
        assertEquals(PreferenceValueTypeUiModel.Str, row.type)
        assertNull(row.collapsible)
    }

    @Test
    fun `valid json object or array is a JSON`() {
        assertEquals(PreferenceValueTypeUiModel.Json, string("""{"a":1}""").type)
        assertEquals(PreferenceValueTypeUiModel.Json, string("""  [1,2] """).type)
    }

    @Test
    fun `text starting with a brace but not json stays a STR`() {
        assertEquals(PreferenceValueTypeUiModel.Str, string("{not json").type)
        assertEquals(PreferenceValueTypeUiModel.Str, string("[1, 2").type)
    }

    @Test
    fun `json is collapsible with item count, size and pretty print`() {
        val raw = """[{"id":1},{"id":2},{"id":3}]"""
        val collapsible = assertNotNull(string(raw).collapsible)
        assertTrue(collapsible.isJson)
        assertEquals("3 items · ${raw.length} B", collapsible.sizeLabel)
        assertEquals(raw, collapsible.preview)
        assertTrue(collapsible.expandedText.contains("\n  {\n    \"id\": 1\n  }"))
    }

    @Test
    fun `object counts its keys and a single item is singular`() {
        assertEquals("2 items", string("""{"a":1,"b":2}""").collapsible!!.sizeLabel.substringBefore(" ·"))
        assertEquals("1 item", string("""[1]""").collapsible!!.sizeLabel.substringBefore(" ·"))
    }

    @Test
    fun `long or multiline text is collapsible without json`() {
        val long = string("a".repeat(100)).collapsible
        assertNotNull(long)
        assertFalse(long.isJson)
        assertEquals("100 B", long.sizeLabel)

        val multiline = string("one\ntwo").collapsible
        assertNotNull(multiline)
        assertEquals("one two", multiline.preview)
    }

    @Test
    fun `byte size label`() {
        assertEquals("0 B", byteSizeLabel(0))
        assertEquals("1023 B", byteSizeLabel(1023))
        assertEquals("1.0 KB", byteSizeLabel(1024))
        assertEquals("2.5 KB", byteSizeLabel(2560))
    }

    @Test
    fun `interval label`() {
        assertEquals("1s", delayLabel(1000))
        assertEquals("10s", delayLabel(10_000))
        assertEquals("1500ms", delayLabel(1500))
        assertEquals("3s", PreferenceAutoUpdate.Enabled(3000).delayLabel)
        assertEquals(listOf("1s", "2s", "5s", "10s"), autoUpdateDelayPresets().map { it.label })
    }

    @Test
    fun `short json starts open, long json is collapsible`() {
        // 3 lines pretty printed
        assertTrue(string("""{"a":1}""").collapsible!!.alwaysOpen)
        // 5 lines: still open
        assertTrue(string("""{"a":1,"b":2,"c":3}""").collapsible!!.alwaysOpen)
        // 6 lines: collapsible
        assertFalse(string("""{"a":1,"b":2,"c":3,"d":4}""").collapsible!!.alwaysOpen)
        // long text is never open
        assertFalse(string("a".repeat(100)).collapsible!!.alwaysOpen)
    }
}
