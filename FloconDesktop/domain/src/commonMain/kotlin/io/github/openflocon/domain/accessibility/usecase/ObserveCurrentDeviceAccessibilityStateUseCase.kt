package io.github.openflocon.domain.accessibility.usecase

import io.github.openflocon.domain.accessibility.models.AccessibilityStateDomainModel
import io.github.openflocon.domain.accessibility.repository.AccessibilityRepository
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveCurrentDeviceAccessibilityStateUseCase(
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val accessibilityRepository: AccessibilityRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<AccessibilityStateDomainModel?> = observeCurrentDeviceIdAndPackageNameUseCase()
        .flatMapLatest { deviceIdAndPackageName ->
            if (deviceIdAndPackageName == null) {
                flowOf(null)
            } else {
                accessibilityRepository.observeState(deviceIdAndPackageName)
            }
        }
}
