package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.models.DeviceId
import kotlinx.coroutines.flow.Flow

class ObserveCurrentDeviceIdUseCase(
    private val currentDeviceIdProvider: CurrentDeviceIdProvider,
) {
    operator fun invoke(): Flow<DeviceId?> = currentDeviceIdProvider.observe()
}
