package io.github.openflocon.flocon.accessibility

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TalkBackServicesTest {

    private val google = "com.google.android.marvin.talkback/com.google.android.marvin.talkback.TalkBackService"
    private val samsung = "com.samsung.android.accessibility.talkback/com.samsung.android.marvin.talkback.TalkBackService"
    private val other = "com.other/.Service"

    @Test
    fun `finds the Google TalkBack`() {
        assertEquals(google, findTalkBackService { it == "com.google.android.marvin.talkback" })
    }

    @Test
    fun `finds the Samsung TalkBack`() {
        assertEquals(samsung, findTalkBackService { it == "com.samsung.android.accessibility.talkback" })
    }

    @Test
    fun `no TalkBack installed`() {
        assertNull(findTalkBackService { false })
    }

    @Test
    fun `nothing enabled yet gives only TalkBack`() {
        assertEquals(google, withService(null, google))
        assertEquals(google, withService("null", google))
        assertEquals(google, withService("", google))
    }

    @Test
    fun `the services already enabled are kept`() {
        assertEquals("$other:$google", withService("$other\n", google))
    }

    @Test
    fun `TalkBack is not added twice`() {
        assertEquals("$other:$google", withService("$other:$google", google))
    }

    @Test
    fun `removing TalkBack keeps the other services`() {
        assertEquals(other, withoutTalkBack("$other:$google"))
        assertEquals(other, withoutTalkBack("$samsung:$other"))
    }

    @Test
    fun `removing the only TalkBack leaves nothing`() {
        assertEquals("", withoutTalkBack(google))
        assertEquals("", withoutTalkBack(null))
        assertEquals("", withoutTalkBack("null"))
    }

    @Test
    fun `a short form TalkBack entry is removed too`() {
        assertEquals(other, withoutTalkBack("com.google.android.marvin.talkback/.TalkBackService:$other"))
    }
}
