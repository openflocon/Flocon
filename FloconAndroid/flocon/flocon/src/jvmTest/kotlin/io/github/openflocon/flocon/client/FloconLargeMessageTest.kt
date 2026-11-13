package io.github.openflocon.flocon.client

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FloconLargeMessageTest {

    @Test
    fun `a small message stays on the websocket`() {
        assertFalse(isTooLargeForWebSocket("{\"plugin\":\"network\"}"))
    }

    @Test
    fun `an ascii message is large only above the threshold`() {
        assertFalse(isTooLargeForWebSocket("a".repeat(LARGE_MESSAGE_THRESHOLD_BYTES)))
        assertTrue(isTooLargeForWebSocket("a".repeat(LARGE_MESSAGE_THRESHOLD_BYTES + 1)))
    }

    @Test
    fun `the size is counted in utf8 bytes, not in chars`() {
        // 3 bytes per char: under the threshold in chars, over it in bytes
        val chars = LARGE_MESSAGE_THRESHOLD_BYTES / 3 + 1
        assertTrue(isTooLargeForWebSocket("€".repeat(chars)))
    }
}
