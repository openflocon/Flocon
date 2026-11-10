package io.github.openflocon.domain.accessibility.usecase

import io.github.openflocon.domain.accessibility.repository.AccessibilityRepository
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * The types of the events received, to filter on
 */
class ObserveCurrentDeviceAccessibilityTypesUseCase(
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val accessibilityRepository: AccessibilityRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<String>> = observeCurrentDeviceIdAndPackageNameUseCase()
        .flatMapLatest { deviceIdAndPackageName ->
            if (deviceIdAndPackageName == null) {
                flowOf(emptyList())
            } else {
                accessibilityRepository.observeTypes(deviceIdAndPackageName)
            }
        }
}
