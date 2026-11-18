package io.github.openflocon.domain.accessibility.usecase

import io.github.openflocon.domain.accessibility.repository.AccessibilityRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase

class ResetCurrentDeviceAccessibilityEventsUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val accessibilityRepository: AccessibilityRepository,
) {
    suspend operator fun invoke() {
        val deviceIdAndPackageName = getCurrentDeviceIdAndPackageNameUseCase() ?: return
        accessibilityRepository.clearEvents(deviceIdAndPackageName)
    }
}
