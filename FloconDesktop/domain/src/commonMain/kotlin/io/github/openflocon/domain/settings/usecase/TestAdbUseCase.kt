package io.github.openflocon.domain.settings.usecase

import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.common.Either

class TestAdbUseCase(
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
) {
    suspend operator fun invoke(adbPathOverride: String? = null): Either<Throwable, Unit> = executeAdbCommandUseCase(
        command = "start-server",
        target = AdbCommandTargetDomainModel.AllDevices,
        adbPathOverride = adbPathOverride,
    ).mapSuccess { Unit }
}
