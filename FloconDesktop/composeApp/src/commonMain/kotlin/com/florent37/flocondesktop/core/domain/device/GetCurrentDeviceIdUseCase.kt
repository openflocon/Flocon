package com.florent37.flocondesktop.core.domain.device

import com.florent37.flocondesktop.DeviceId

class GetCurrentDeviceIdUseCase(
    private val getCurrentDeviceUseCase: GetCurrentDeviceUseCase,
) {
    operator fun invoke(): DeviceId? = getCurrentDeviceUseCase()?.deviceId
}
