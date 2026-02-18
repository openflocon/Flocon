package io.github.openflocon.domain.adbcommander.usecase

import io.github.openflocon.domain.adbcommander.models.AdbFlowDomainModel
import io.github.openflocon.domain.adbcommander.repository.AdbCommanderRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdUseCase

class SaveFlowUseCase(
    private val adbCommanderRepository: AdbCommanderRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
) {
    suspend operator fun invoke(flow: AdbFlowDomainModel): Long? {
        val deviceId = getCurrentDeviceIdUseCase() ?: ExecuteAdbCommanderCommandUseCase.DEFAULT_DEVICE_ID
        return adbCommanderRepository.saveFlow(deviceId, flow)
    }
}
