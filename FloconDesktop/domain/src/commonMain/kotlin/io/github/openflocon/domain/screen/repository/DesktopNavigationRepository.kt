package io.github.openflocon.domain.screen.repository

import io.github.openflocon.domain.screen.models.FloconScreen
import io.github.openflocon.domain.screen.models.ScreenRequestDomainModel
import kotlinx.coroutines.flow.Flow

/**
 * The screen the Flocon window is asked to display, held until the UI applies it.
 * A new request replaces the pending one.
 */
interface DesktopNavigationRepository {
    fun requestScreen(screen: FloconScreen)

    /** The requests to apply, the expired ones are never emitted */
    fun observeRequestedScreen(): Flow<ScreenRequestDomainModel>

    /** Clears [request] once applied, unless a newer one replaced it meanwhile */
    fun consumeRequestedScreen(request: ScreenRequestDomainModel)
}
