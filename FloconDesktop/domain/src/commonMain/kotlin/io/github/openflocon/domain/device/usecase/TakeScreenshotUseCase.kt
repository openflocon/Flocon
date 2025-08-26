package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.then
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

class TakeScreenshotUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
) {

    suspend operator fun invoke() : Either<Throwable, String> {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return Failure(Throwable("No device selected"))

        val fileName = "screenshot_${current.packageName}_${System.currentTimeMillis()}.png"
        val onDeviceFilePath = "/sdcard/$fileName"
        val desktopPath = Paths.get(System.getProperty("user.home"), "Desktop", fileName).absolutePathString()

        return executeAdbCommandUseCase(
            target = AdbCommandTargetDomainModel.Device(current.deviceId),
            command = "shell screencap -p $onDeviceFilePath",
        ).then {
            executeAdbCommandUseCase(
                target = AdbCommandTargetDomainModel.Device(current.deviceId),
                command = "pull $onDeviceFilePath $desktopPath",
            )
        }.then {
            executeAdbCommandUseCase(
                target = AdbCommandTargetDomainModel.Device(current.deviceId),
                command = "shell rm $onDeviceFilePath",
            )
        }.mapSuccess {
            desktopPath
        }.alsoFailure { it.printStackTrace() }
    }
}
