package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.device.models.RecordingDomainModel
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

class StopRecordingVideoUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
) {

    suspend operator fun invoke(recording: RecordingDomainModel) : Either<Throwable, String> {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return Failure(Throwable("No device selected"))

        val fileName = "record_${current.packageName}_${System.currentTimeMillis()}.mp4"

        executeAdbCommandUseCase(
            target = AdbCommandTargetDomainModel.Device(current.deviceId),
            command = "shell pkill -l 2 ${recording.processName}",
        )

        // wait until the record is done
        recording.completableDeferred.await()

        val desktopPath = Paths.get(System.getProperty("user.home"), "Desktop", fileName).absolutePathString()

        executeAdbCommandUseCase(
            target = AdbCommandTargetDomainModel.Device(current.deviceId),
            command = "pull ${recording.onDeviceFilePath} $desktopPath",
        )

        executeAdbCommandUseCase(
            target = AdbCommandTargetDomainModel.Device(current.deviceId),
            command = "shell rm ${recording.onDeviceFilePath}",
        )

        return Success(desktopPath)
    }
}
