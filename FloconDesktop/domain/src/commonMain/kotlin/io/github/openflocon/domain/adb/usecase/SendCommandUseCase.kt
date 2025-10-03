package io.github.openflocon.domain.adb.usecase

import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.firstOrNull

class SendCommandUseCase(
    private val adbRepository: AdbRepository,
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(
        deviceSerial: String,
        vararg args: String
    ): Either<Throwable, String> = Either.catch {
        return adbRepository.executeAdbCommand(
            adbPath = settingsRepository.adbPath.firstOrNull() ?: throw Throwable("error"),
            command = args.joinToString(separator = " "),
            deviceSerial = deviceSerial
        )
    }

}
