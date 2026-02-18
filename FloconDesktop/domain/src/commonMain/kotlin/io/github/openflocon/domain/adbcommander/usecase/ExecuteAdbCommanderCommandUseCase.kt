package io.github.openflocon.domain.adbcommander.usecase

import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.adbcommander.repository.AdbCommanderRepository
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdUseCase

class ExecuteAdbCommanderCommandUseCase(
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
    private val adbCommanderRepository: AdbCommanderRepository,
) {
    suspend operator fun invoke(command: String): Either<Throwable, String> {
        val deviceId = getCurrentDeviceIdUseCase()
        val storageDeviceId = deviceId ?: DEFAULT_DEVICE_ID

        val cleanCommand = command.trimStart().removePrefix("adb ").trimStart()

        val target = if (deviceId != null) {
            AdbCommandTargetDomainModel.Device(deviceId)
        } else {
            AdbCommandTargetDomainModel.AllDevices
        }

        val result = executeAdbCommandUseCase(
            target = target,
            command = cleanCommand,
        )

        result.fold(
            doOnFailure = { error ->
                adbCommanderRepository.addToHistory(
                    deviceId = storageDeviceId,
                    command = cleanCommand,
                    output = error.message ?: "Unknown error",
                    isSuccess = false,
                )
            },
            doOnSuccess = { output ->
                adbCommanderRepository.addToHistory(
                    deviceId = storageDeviceId,
                    command = cleanCommand,
                    output = output,
                    isSuccess = true,
                )
            },
        )

        return result
    }

    companion object {
        const val DEFAULT_DEVICE_ID = "_default"
    }
}
