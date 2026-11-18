package io.github.openflocon.domain.accessibility.usecase

import io.github.openflocon.domain.accessibility.models.AccessibilityFocusedNodeDomainModel
import io.github.openflocon.domain.accessibility.repository.AccessibilityRepository
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveCurrentDeviceAccessibilityFocusedNodeUseCase(
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val accessibilityRepository: AccessibilityRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<AccessibilityFocusedNodeDomainModel?> = observeCurrentDeviceIdAndPackageNameUseCase()
        .flatMapLatest { deviceIdAndPackageName ->
            if (deviceIdAndPackageName == null) {
                flowOf(null)
            } else {
                accessibilityRepository.observeFocusedNode(deviceIdAndPackageName)
            }
        }
}
