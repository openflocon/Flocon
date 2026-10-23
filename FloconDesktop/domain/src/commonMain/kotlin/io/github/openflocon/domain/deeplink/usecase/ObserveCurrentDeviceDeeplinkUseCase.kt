package io.github.openflocon.domain.deeplink.usecase

import io.github.openflocon.domain.deeplink.models.Deeplinks
import io.github.openflocon.domain.deeplink.repository.DeeplinkRepository
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveCurrentDeviceDeeplinkUseCase(
    private val deeplinkRepository: DeeplinkRepository,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(): Flow<Deeplinks> = observeCurrentDeviceIdAndPackageNameUseCase().flatMapLatest { current ->
        if (current == null) {
            flowOf(Deeplinks(emptyList(), emptyList()))
        } else {
            deeplinkRepository.observe(deviceIdAndPackageName = current)
        }
    }
}
