package io.github.openflocon.flocondesktop.core.domain.device

import com.flocon.data.remote.models.DeviceId

class GetCurrentDeviceIdUseCase(
    private val getCurrentDeviceUseCase: GetCurrentDeviceUseCase,
) {
    operator fun invoke(): DeviceId? = getCurrentDeviceUseCase()?.deviceId
}
