package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.models.DeviceId

class GetCurrentDeviceIdUseCase(
    private val getCurrentDeviceUseCase: GetCurrentDeviceUseCase,
) {
    operator fun invoke(): DeviceId? = getCurrentDeviceUseCase()?.deviceId
}
