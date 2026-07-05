package io.github.openflocon.domain.adbcommander.usecase

import io.github.openflocon.domain.adbcommander.models.AdbCommandHistoryDomainModel
import io.github.openflocon.domain.adbcommander.repository.AdbCommanderRepository
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

class ObserveCommandHistoryUseCase(
    private val adbCommanderRepository: AdbCommanderRepository,
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
) {
    operator fun invoke(): Flow<List<AdbCommandHistoryDomainModel>> =
        observeCurrentDeviceIdUseCase().flatMapLatest { deviceId ->
            adbCommanderRepository.observeHistory(
                deviceId ?: ExecuteAdbCommanderCommandUseCase.DEFAULT_DEVICE_ID
            )
        }
}
