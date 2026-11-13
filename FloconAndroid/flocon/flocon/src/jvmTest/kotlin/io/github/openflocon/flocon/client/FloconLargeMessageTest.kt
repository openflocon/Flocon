package io.github.openflocon.flocon.client

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FloconLargeMessageTest {

    @Test
    fun `a small message stays on the websocket`() {
        assertFalse("{\"plugin\":\"network\"}".isTooLargeForWebSocket())
    }

    @Test
    fun `an ascii message is large only above the threshold`() {
        assertFalse("a".repeat(LARGE_MESSAGE_THRESHOLD_BYTES).isTooLargeForWebSocket())
        assertTrue("a".repeat(LARGE_MESSAGE_THRESHOLD_BYTES + 1).isTooLargeForWebSocket())
    }

    @Test
    fun `the size is counted in utf8 bytes, not in chars`() {
        // 3 bytes per char: under the threshold in chars, over it in bytes
        val chars = LARGE_MESSAGE_THRESHOLD_BYTES / 3 + 1
        assertTrue("€".repeat(chars).isTooLargeForWebSocket())
    }
}
