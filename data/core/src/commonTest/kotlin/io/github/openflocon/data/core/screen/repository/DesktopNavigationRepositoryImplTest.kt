package io.github.openflocon.data.core.screen.repository

import io.github.openflocon.domain.screen.models.FloconScreen
import io.github.openflocon.domain.screen.models.ScreenRequestDomainModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DesktopNavigationRepositoryImplTest {

    private var time = 1_000L
    private val repository = DesktopNavigationRepositoryImpl { time }

    private fun pending(): ScreenRequestDomainModel? = runBlocking {
        withTimeoutOrNull(100) { repository.observeRequestedScreen().first() }
    }

    @Test
    fun `a request becomes pending`() {
        repository.requestScreen(FloconScreen.Network)

        assertEquals(FloconScreen.Network, pending()?.screen)
    }

    @Test
    fun `nothing is emitted without a request`() {
        assertNull(pending())
    }

    @Test
    fun `a newer request replaces the pending one`() {
        repository.requestScreen(FloconScreen.Network)
        time += 10
        repository.requestScreen(FloconScreen.Database)

        assertEquals(FloconScreen.Database, pending()?.screen)
    }

    @Test
    fun `consuming the pending request clears it`() {
        repository.requestScreen(FloconScreen.Network)

        repository.consumeRequestedScreen(pending()!!)

        assertNull(pending())
    }

    @Test
    fun `consuming an older request does not clear a newer one`() {
        repository.requestScreen(FloconScreen.Network)
        val older = pending()!!
        time += 10
        repository.requestScreen(FloconScreen.Database)

        repository.consumeRequestedScreen(older)

        assertEquals(FloconScreen.Database, pending()?.screen)
    }

    @Test
    fun `a request older than the ttl is not emitted anymore`() {
        repository.requestScreen(FloconScreen.Network)

        time += SCREEN_REQUEST_TTL_MS + 1

        assertNull(pending())
    }

    @Test
    fun `a request exactly at the ttl is still emitted`() {
        repository.requestScreen(FloconScreen.Network)

        time += SCREEN_REQUEST_TTL_MS

        assertEquals(FloconScreen.Network, pending()?.screen)
    }
}
