package io.github.openflocon.domain.adbcommander.usecase

import io.github.openflocon.domain.adbcommander.repository.AdbCommanderRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdUseCase

class ClearCommandHistoryUseCase(
    private val adbCommanderRepository: AdbCommanderRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
) {
    suspend operator fun invoke() {
        val deviceId = getCurrentDeviceIdUseCase() ?: ExecuteAdbCommanderCommandUseCase.DEFAULT_DEVICE_ID
        adbCommanderRepository.clearHistory(deviceId)
    }
}
