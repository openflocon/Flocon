package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.then
import java.io.File
import java.nio.file.Paths

class TakeScreenshotUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
) {

    suspend operator fun invoke(): Either<Throwable, String> {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return Failure(Throwable("No device selected"))

        val fileName = "screenshot_${current.packageName}_${System.currentTimeMillis()}.png"
        val onDeviceFilePath = "/sdcard/$fileName"

        val parentFile = Paths.get(System.getProperty("user.home"), "Desktop", "Flocon", "Screenshot").toFile()
        if (!parentFile.exists()) {
            parentFile.mkdirs()
        }

        val filePath = File(parentFile, fileName).absolutePath

        return executeAdbCommandUseCase(
            target = AdbCommandTargetDomainModel.Device(current.deviceId),
            command = "shell screencap -p $onDeviceFilePath",
        ).then {
            executeAdbCommandUseCase(
                target = AdbCommandTargetDomainModel.Device(current.deviceId),
                command = "pull $onDeviceFilePath $filePath",
            )
        }.then {
            executeAdbCommandUseCase(
                target = AdbCommandTargetDomainModel.Device(current.deviceId),
                command = "shell rm $onDeviceFilePath",
            )
        }.mapSuccess {
            filePath
        }.alsoFailure { it.printStackTrace() }
    }
}
