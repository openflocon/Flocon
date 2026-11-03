package io.github.openflocon.domain.screen

import io.github.openflocon.domain.screen.models.FloconScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class ScreenRequestStoreTest {

    private var time = 1_000L
    private val store = ScreenRequestStore { time }

    @Test
    fun `a request becomes pending`() {
        store.request(FloconScreen.Network)

        assertEquals(FloconScreen.Network, store.peek()?.screen)
    }

    @Test
    fun `the pending request is emitted to the observers`() = runBlocking {
        store.request(FloconScreen.Timeline)

        assertEquals(FloconScreen.Timeline, store.pending.first().screen)
    }

    @Test
    fun `a newer request replaces the pending one`() {
        store.request(FloconScreen.Network)
        time += 10
        store.request(FloconScreen.Database)

        assertEquals(FloconScreen.Database, store.peek()?.screen)
    }

    @Test
    fun `consuming the pending request clears it`() {
        store.request(FloconScreen.Network)
        val request = store.peek()!!

        store.consume(request)

        assertNull(store.peek())
    }

    @Test
    fun `consuming an older request does not clear a newer one`() {
        store.request(FloconScreen.Network)
        val older = store.peek()!!
        time += 10
        store.request(FloconScreen.Database)

        store.consume(older)

        assertNotEquals(null, store.peek())
        assertEquals(FloconScreen.Database, store.peek()?.screen)
    }

    @Test
    fun `a request older than the ttl is not pending anymore`() {
        store.request(FloconScreen.Network)

        time += SCREEN_REQUEST_TTL_MS + 1

        assertNull(store.peek())
    }

    @Test
    fun `a request exactly at the ttl is still pending`() {
        store.request(FloconScreen.Network)

        time += SCREEN_REQUEST_TTL_MS

        assertEquals(FloconScreen.Network, store.peek()?.screen)
    }
}
