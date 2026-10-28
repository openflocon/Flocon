package com.flocon.data.remote.accessibility.datasource

import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AccessibilityRemoteDataSourceImplTest {

    private val dataSource = AccessibilityRemoteDataSourceImpl(Json { ignoreUnknownKeys = true })

    private fun message(method: String, body: String) = FloconIncomingMessageDomainModel(
        deviceName = "Pixel 9",
        deviceId = "pixel",
        plugin = "accessibility",
        body = body,
        method = method,
        appName = "Sample",
        appPackageName = "io.flocon.sample",
        appInstance = 1L,
        platform = "android",
        floconVersionOnDevice = "1.0.0",
    )

    @Test
    fun `the events are decoded, the optional fields may be absent`() {
        val events = dataSource.getEvents(
            message(
                method = "addevents",
                body = """
                    [
                      {"id":"1","type":"VIEW_FOCUSED","text":"Add to cart","contentDescription":null,"className":"android.widget.Button",
                       "screen":"CheckoutActivity","bounds":"[0,0][100,48]","extras":{"checked":"false"},"createdAt":1000},
                      {"id":"2","type":"ANNOUNCEMENT","text":"Added","createdAt":2000,"unknown":"ignored"}
                    ]
                """.trimIndent(),
            )
        )

        assertEquals(2, events.size)
        assertEquals("Add to cart", events[0].text)
        assertNull(events[0].contentDescription)
        assertEquals("CheckoutActivity", events[0].screen)
        assertEquals(mapOf("checked" to "false"), events[0].extras)
        assertEquals(1000L, events[0].createdAt)
        assertEquals("ANNOUNCEMENT", events[1].type)
        assertNull(events[1].className)
        assertTrue(events[1].extras.isEmpty())
    }

    @Test
    fun `an invalid body gives no event`() {
        assertTrue(dataSource.getEvents(message("addevents", "not json")).isEmpty())
    }

    @Test
    fun `the state is decoded`() {
        val state = dataSource.getState(
            message("state", """{"supported":true,"enabled":true,"touchExploration":true,"services":["TalkBack"]}""")
        )

        assertNotNull(state)
        assertTrue(state.supported)
        assertTrue(state.enabled)
        assertTrue(state.touchExploration)
        assertEquals(listOf("TalkBack"), state.services)
    }

    @Test
    fun `an invalid state is ignored`() {
        assertNull(dataSource.getState(message("state", "[]")))
    }

    @Test
    fun `the focused node is decoded, the empty body is nothing focused`() {
        val focused = dataSource.getFocusedNode(
            message(
                method = "focusednode",
                body = """{"found":true,"text":"Add to cart","className":"Button","bounds":"[0,0][100,48]","index":2,"count":12,"hasNext":true}""",
            )
        )

        assertNotNull(focused)
        assertTrue(focused.found)
        assertEquals("Add to cart", focused.text)
        assertEquals(2, focused.index)
        assertEquals(12, focused.count)
        assertTrue(focused.hasNext)
        assertEquals(false, focused.hasPrevious)

        val nothing = dataSource.getFocusedNode(message(method = "focusednode", body = "{}"))
        assertNotNull(nothing)
        assertEquals(false, nothing.found)
    }
}
