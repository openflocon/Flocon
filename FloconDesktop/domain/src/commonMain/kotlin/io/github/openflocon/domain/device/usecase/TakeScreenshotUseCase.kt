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

    suspend operator fun invoke() : Either<Throwable, Unit> {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return Failure(Throwable("No device selected"))

        val fileName = "screenshot_${current.packageName}_${System.currentTimeMillis()}.png"
        val onDeviceFilePath = "/sdcard/$fileName"
        val desktopPath = Paths.get(System.getProperty("user.home"), "Desktop", fileName)

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
        }.mapSuccess {  }
            .alsoFailure { it.printStackTrace() }
    }
}

// TODO move to a repo

private fun getDesktopPath(): String {
    val userHome = System.getProperty("user.home")
    return "$userHome${File.separator}Desktop"
}

private fun createDesktopFile(fileName: String) : File? {
    val desktopPath = getDesktopPath()
    val file = File(desktopPath, fileName)

    try {
        if (!file.exists()) {
            file.createNewFile()
        }
        println("Fichier créé avec succès sur le bureau : ${file.absolutePath}")
        return file
    } catch (e: Exception) {
        println("Erreur lors de la création du fichier : ${e.message}")
        return null
    }
}
