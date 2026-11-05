package io.github.openflocon.domain.screen.usecase

import io.github.openflocon.domain.screen.models.FloconScreen
import io.github.openflocon.domain.screen.repository.DesktopNavigationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

/** The screens the Flocon window is asked to display, each request is delivered once */
class ObserveRequestedDesktopScreenUseCase(
    private val desktopNavigationRepository: DesktopNavigationRepository,
) {
    operator fun invoke(): Flow<FloconScreen> = desktopNavigationRepository.observeRequestedScreen()
        .onEach(desktopNavigationRepository::consumeRequestedScreen)
        .map { it.screen }
}
