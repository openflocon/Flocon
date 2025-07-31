package io.github.openflocon.flocondesktop

import io.github.openflocon.flocondesktop.features.deeplinks.ui.mapper.parseDeeplinkString
import io.github.openflocon.flocondesktop.features.deeplinks.ui.model.DeeplinkPart
import kotlin.test.Test
import kotlin.test.assertEquals

class DeeplinkParserTest {

    // Cas de test standard avec texte et champs
    @Test
    fun testInterleavedTextAndTextFields() {
        val input = "text[value1]tototot[value2]tata"
        val expected = listOf(
            DeeplinkPart.Text("text"),
            DeeplinkPart.TextField("value1"),
            DeeplinkPart.Text("tototot"),
            DeeplinkPart.TextField("value2"),
            DeeplinkPart.Text("tata"),
        )
        val actual = parseDeeplinkString(input)
        assertEquals(expected, actual)
    }

    // Cas où la chaîne commence et se termine par un champ
    @Test
    fun testStartsAndEndsWithTextField() {
        val input = "[first]no text at start[second]"
        val expected = listOf(
            DeeplinkPart.TextField("first"),
            DeeplinkPart.Text("no text at start"),
            DeeplinkPart.TextField("second"),
        )
        val actual = parseDeeplinkString(input)
        assertEquals(expected, actual)
    }

    // Cas où la chaîne ne contient que du texte
    @Test
    fun testOnlyText() {
        val input = "only plain text here"
        val expected = listOf(
            DeeplinkPart.Text("only plain text here"),
        )
        val actual = parseDeeplinkString(input)
        assertEquals(expected, actual)
    }

    // Cas où la chaîne ne contient qu'un champ
    @Test
    fun testOnlyTextField() {
        val input = "[onlyfield]"
        val expected = listOf(
            DeeplinkPart.TextField("onlyfield"),
        )
        val actual = parseDeeplinkString(input)
        assertEquals(expected, actual)
    }

    // Cas de la chaîne vide
    @Test
    fun testEmptyString() {
        val input = ""
        val expected = emptyList<DeeplinkPart>()
        val actual = parseDeeplinkString(input)
        assertEquals(expected, actual)
    }

    // Cas où un champ est vide (ex: [])
    @Test
    fun testEmptyPlaceholder() {
        val input = "before[]after"
        val expected = listOf(
            DeeplinkPart.Text("before"),
            DeeplinkPart.TextField(""), // Placeholder vide
            DeeplinkPart.Text("after"),
        )
        val actual = parseDeeplinkString(input)
        assertEquals(expected, actual)
    }

    // Cas avec des caractères spéciaux à l'intérieur des crochets
    @Test
    fun testSpecialCharactersInPlaceholder() {
        val input = "path[param/1?id=abc]suffix"
        val expected = listOf(
            DeeplinkPart.Text("path"),
            DeeplinkPart.TextField("param/1?id=abc"), // Contient des caractères spéciaux
            DeeplinkPart.Text("suffix"),
        )
        val actual = parseDeeplinkString(input)
        assertEquals(expected, actual)
    }

    @Test
    fun testMultipleConsecutiveTextFields() {
        val input = "start[field1][field2]end"
        val expected = listOf(
            DeeplinkPart.Text("start"),
            DeeplinkPart.TextField("field1"),
            DeeplinkPart.TextField("field2"),
            DeeplinkPart.Text("end"),
        )
        val actual = parseDeeplinkString(input)
        assertEquals(expected, actual)
    }

    @Test
    fun testOnlyTextFieldAtStart() {
        val input = "[myField]"
        val expected = listOf(
            DeeplinkPart.TextField("myField"),
        )
        val actual = parseDeeplinkString(input)
        assertEquals(expected, actual)
    }
}
