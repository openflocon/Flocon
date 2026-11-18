package io.github.openflocon.flocondesktop

import io.github.openflocon.flocondesktop.features.deeplinks.mapper.buildLink
import io.github.openflocon.flocondesktop.features.deeplinks.mapper.parseFreeformLink
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkPart
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkVariableViewState
import kotlin.test.Test
import kotlin.test.assertEquals

class DeeplinkLinkBuilderTest {

    private val variables = listOf(
        DeeplinkVariableViewState(name = "env", description = null, value = "staging"),
        DeeplinkVariableViewState(name = "userId", description = null, value = ""),
    )

    @Test
    fun `build link replaces the fields and keeps the resolved variables`() {
        val field = DeeplinkPart.TextField("productId")
        val parts = listOf(
            DeeplinkPart.Text("myapp://product/"),
            field,
            DeeplinkPart.Text("?env="),
            DeeplinkPart.Variable(value = "staging", name = "env"),
        )

        assertEquals("myapp://product/4821?env=staging", parts.buildLink(mapOf(field to "4821")))
    }

    @Test
    fun `freeform link resolves the declared variables`() {
        val parts = parseFreeformLink("myapp://home?env={env}&x=1", variables)

        assertEquals(
            listOf(
                DeeplinkPart.Text("myapp://home?env="),
                DeeplinkPart.Variable(value = "staging", name = "env", isResolved = true),
                DeeplinkPart.Text("&x=1"),
            ),
            parts,
        )
        assertEquals("myapp://home?env=staging&x=1", parts.buildLink())
    }

    @Test
    fun `freeform link flags a variable without value`() {
        val parts = parseFreeformLink("myapp://profile/{userId}", variables)

        assertEquals(
            DeeplinkPart.Variable(value = "{userId}", name = "userId", isResolved = false),
            parts.filterIsInstance<DeeplinkPart.Variable>().single(),
        )
    }

    @Test
    fun `freeform link keeps the braces that are not a declared variable`() {
        val parts = parseFreeformLink("myapp://x/{unknown}", variables)

        assertEquals("myapp://x/{unknown}", parts.buildLink())
        assertEquals(emptyList(), parts.filterIsInstance<DeeplinkPart.Variable>())
    }
}
