package io.github.openflocon.domain.accessibility.usecase

import io.github.openflocon.domain.accessibility.models.AccessibilityEventDomainModel
import io.github.openflocon.domain.accessibility.repository.AccessibilityRepository
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveCurrentDeviceAccessibilityEventsUseCase(
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val accessibilityRepository: AccessibilityRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(type: String?, text: String?): Flow<List<AccessibilityEventDomainModel>> = observeCurrentDeviceIdAndPackageNameUseCase()
        .flatMapLatest { deviceIdAndPackageName ->
            if (deviceIdAndPackageName == null) {
                flowOf(emptyList())
            } else {
                accessibilityRepository.observeEvents(
                    deviceIdAndPackageName = deviceIdAndPackageName,
                    type = type,
                    text = text,
                )
            }
        }
}
