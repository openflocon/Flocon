package io.github.openflocon.domain.screen

import io.github.openflocon.domain.screen.models.FloconScreen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull

// a request not applied after that long (the window was closed, for example) is dropped, not applied hours later
const val SCREEN_REQUEST_TTL_MS = 600_000L

data class ScreenRequest(
    val screen: FloconScreen,
    val requestedAtMs: Long,
)

/**
 * The screen the Flocon window is asked to display, held until the UI applies it.
 * A new request replaces the pending one.
 */
class ScreenRequestStore(
    private val now: () -> Long = { System.currentTimeMillis() },
) {
    private val state = MutableStateFlow<ScreenRequest?>(null)

    /** The requests to apply, the expired ones are never emitted */
    val pending: Flow<ScreenRequest> = state
        .filterNotNull()
        .filter { !it.isExpired() }

    fun request(screen: FloconScreen) {
        state.value = ScreenRequest(screen = screen, requestedAtMs = now())
    }

    /** The request to apply, null if there is none or if it expired */
    fun peek(): ScreenRequest? = state.value?.takeIf { !it.isExpired() }

    /** Clears [request] once applied, unless a newer one replaced it meanwhile */
    fun consume(request: ScreenRequest) {
        state.compareAndSet(request, null)
    }

    private fun ScreenRequest.isExpired() = now() - requestedAtMs > SCREEN_REQUEST_TTL_MS
}
