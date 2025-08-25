package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure

class TakeScreenshotUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
) {
    suspend operator fun invoke() : Either<Throwable, Unit> {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return Failure(Throwable("No device selected"))

        val userHome = System.getProperty("user.home")

        // TODO windows adb exec-out screencap -p > C:\Users\VotreNomUtilisateur\Desktop\capture.png
        return executeAdbCommandUseCase(
            target = AdbCommandTargetDomainModel.Device(current.deviceId),
            command = "exec-out screencap -p > $userHome/Desktop/test.png", //${current.packageName}_${System.currentTimeMillis()}.png",
        ).mapSuccess {
            println(it)
        }
            .alsoFailure { it.printStackTrace() }
    }
}
