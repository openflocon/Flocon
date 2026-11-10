package com.flocon.data.remote.mock

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.time.Clock

class MockScenariosTest {

    private val encoder = MockMessageEncoder(Json { ignoreUnknownKeys = true })

    @Test
    fun `bundled scenarios are registered`() {
        assertEquals(
            listOf(
                "default",
                "doc-network",
                "doc-timeline",
                "doc-crashreporter",
                "doc-analytics",
                "doc-featureflags",
                "doc-deeplinks",
                "empty",
                "multi-device",
                "weird",
                "stress-timeline",
            ),
            MockScenarios.all.map { it.name },
        )
    }

    @Test
    fun `names are unique`() {
        val names = MockScenarios.all.map { it.name }
        assertEquals(names.distinct(), names)
    }

    @Test
    fun `byName finds each scenario`() {
        MockScenarios.all.forEach { assertSame(it, MockScenarios.byName(it.name)) }
    }

    @Test
    fun `byName returns null for unknown`() {
        assertNull(MockScenarios.byName("nope"))
    }

    @Test
    fun `every scenario has a device and a description`() {
        MockScenarios.all.forEach {
            assertTrue(it.devices.isNotEmpty(), "${it.name} has no device")
            assertTrue(it.description.isNotBlank(), "${it.name} has no description")
        }
    }

    @Test
    fun `every scenario encodes`() {
        MockScenarios.all.forEach { encoder.encode(it, Clock.System.now(), "9.9.9") }
    }

    @Test
    fun `empty scenario has no message`() {
        assertTrue(encoder.encode(MockScenarios.byName("empty")!!, Clock.System.now(), "9.9.9").isEmpty())
    }

    @Test
    fun `deeplinks scenario sends the deeplink plugin message`() {
        val messages = encoder.encode(MockScenarios.byName("doc-deeplinks")!!, Clock.System.now(), "9.9.9")
        val message = messages.single()
        assertEquals("deeplink", message.plugin)
        assertEquals("get_deeplinks", message.method)
        assertTrue(message.body.contains("flocon-shop://product/[productId]"))
        assertTrue(message.body.contains("auto_complete"))
    }
}
