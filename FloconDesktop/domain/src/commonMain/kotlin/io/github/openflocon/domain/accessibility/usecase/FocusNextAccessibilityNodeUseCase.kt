package io.github.openflocon.domain.accessibility.usecase

import io.github.openflocon.domain.accessibility.repository.AccessibilityRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase

class FocusNextAccessibilityNodeUseCase(
    private val accessibilityRepository: AccessibilityRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke() {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return

        accessibilityRepository.focusNext(current)
    }
}
