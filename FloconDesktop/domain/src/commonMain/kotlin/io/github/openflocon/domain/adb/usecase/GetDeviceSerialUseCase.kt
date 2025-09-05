package io.github.openflocon.domain.adb.usecase

import io.github.openflocon.domain.adb.repository.AdbRepository

class GetDeviceSerialUseCase internal constructor(
    private val adbRepository: AdbRepository
) {
    suspend operator fun invoke(
        deviceId: String
    ): String {
        return adbRepository.getAdbSerial(deviceId).orEmpty()
    }
}
