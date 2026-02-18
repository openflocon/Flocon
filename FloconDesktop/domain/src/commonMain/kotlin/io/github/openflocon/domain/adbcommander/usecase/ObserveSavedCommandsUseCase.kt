package io.github.openflocon.domain.adbcommander.usecase

import io.github.openflocon.domain.adbcommander.models.AdbCommandDomainModel
import io.github.openflocon.domain.adbcommander.repository.AdbCommanderRepository
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

class ObserveSavedCommandsUseCase(
    private val adbCommanderRepository: AdbCommanderRepository,
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
) {
    operator fun invoke(): Flow<List<AdbCommandDomainModel>> =
        observeCurrentDeviceIdUseCase().flatMapLatest { deviceId ->
            adbCommanderRepository.observeSavedCommands(
                deviceId ?: ExecuteAdbCommanderCommandUseCase.DEFAULT_DEVICE_ID
            )
        }
}
