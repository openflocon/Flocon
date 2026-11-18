package io.github.openflocon.data.core.screen.repository

import io.github.openflocon.domain.screen.models.FloconScreen
import io.github.openflocon.domain.screen.models.ScreenRequestDomainModel
import io.github.openflocon.domain.screen.repository.DesktopNavigationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull

// a request not applied after that long (the window was closed, for example) is dropped, not applied hours later
internal const val SCREEN_REQUEST_TTL_MS = 600_000L

internal class DesktopNavigationRepositoryImpl(
    private val now: () -> Long = { System.currentTimeMillis() },
) : DesktopNavigationRepository {

    private val state = MutableStateFlow<ScreenRequestDomainModel?>(null)

    override fun requestScreen(screen: FloconScreen) {
        state.value = ScreenRequestDomainModel(screen = screen, requestedAtMs = now())
    }

    override fun observeRequestedScreen(): Flow<ScreenRequestDomainModel> = state
        .filterNotNull()
        .filter { !it.isExpired() }

    override fun consumeRequestedScreen(request: ScreenRequestDomainModel) {
        state.compareAndSet(request, null)
    }

    private fun ScreenRequestDomainModel.isExpired() = now() - requestedAtMs > SCREEN_REQUEST_TTL_MS
}
