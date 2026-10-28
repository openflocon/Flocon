package io.github.openflocon.domain.accessibility.usecase

import io.github.openflocon.domain.accessibility.repository.AccessibilityRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase

class FocusPreviousAccessibilityNodeUseCase(
    private val accessibilityRepository: AccessibilityRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke() {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return

        accessibilityRepository.focusPrevious(current)
    }
}
