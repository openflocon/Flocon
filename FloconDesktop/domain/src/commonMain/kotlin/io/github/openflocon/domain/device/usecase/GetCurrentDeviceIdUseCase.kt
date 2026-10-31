package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.models.DeviceId

class GetCurrentDeviceIdUseCase(
    private val currentDeviceIdProvider: CurrentDeviceIdProvider,
) {
    suspend operator fun invoke(): DeviceId? = currentDeviceIdProvider.get()
}
