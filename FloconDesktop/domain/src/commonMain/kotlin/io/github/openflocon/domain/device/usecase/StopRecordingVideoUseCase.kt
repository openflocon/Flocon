package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.adb.usecase.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.device.models.RecordingDomainModel
import java.io.File
import java.nio.file.Paths

class StopRecordingVideoUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
) {

    suspend operator fun invoke(recording: RecordingDomainModel): Either<Throwable, String> {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return Failure(Throwable("No device selected"))

        val fileName = "record_${current.packageName}_${System.currentTimeMillis()}.mp4"

        executeAdbCommandUseCase(
            target = AdbCommandTargetDomainModel.Device(current.deviceId),
            command = "shell pkill -l 2 ${recording.processName}",
        )

        // wait until the record is done
        recording.completableDeferred.await()

        val parentFile = Paths.get(System.getProperty("user.home"), "Desktop", "Flocon", "ScreenRecord").toFile()
        if (!parentFile.exists()) {
            parentFile.mkdirs()
        }

        val filePath = File(parentFile, fileName).absolutePath

        executeAdbCommandUseCase(
            target = AdbCommandTargetDomainModel.Device(current.deviceId),
            command = "pull ${recording.onDeviceFilePath} $filePath",
        )

        executeAdbCommandUseCase(
            target = AdbCommandTargetDomainModel.Device(current.deviceId),
            command = "shell rm ${recording.onDeviceFilePath}",
        )

        return Success(filePath)
    }
}
