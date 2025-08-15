package io.github.openflocon.domain.settings.usecase

import io.github.openflocon.domain.Constant
import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.common.Either

class StartAdbForwardUseCase(
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
) {
    suspend operator fun invoke(): Either<Throwable, Unit> {
        return executeAdbCommandUseCase(
            command = "reverse tcp:${Constant.SERVER_PORT} tcp:${Constant.SERVER_PORT}",
            target = AdbCommandTargetDomainModel.AllDevices,
        ).mapSuccess { }
    }
}
