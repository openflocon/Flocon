package io.github.openflocon.domain.screen.usecase

import io.github.openflocon.domain.screen.models.FloconScreen
import io.github.openflocon.domain.screen.repository.DesktopNavigationRepository

class RequestDesktopScreenUseCase(
    private val desktopNavigationRepository: DesktopNavigationRepository,
) {
    operator fun invoke(screen: FloconScreen) = desktopNavigationRepository.requestScreen(screen)
}
