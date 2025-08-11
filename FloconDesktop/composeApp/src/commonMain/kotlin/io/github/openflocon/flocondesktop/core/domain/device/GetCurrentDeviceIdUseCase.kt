package io.github.openflocon.flocondesktop.core.domain.device

import io.github.openflocon.domain.models.DeviceId

class GetCurrentDeviceIdUseCase(
    private val getCurrentDeviceUseCase: GetCurrentDeviceUseCase,
) {
    operator fun invoke(): DeviceId? = getCurrentDeviceUseCase()?.deviceId
}
