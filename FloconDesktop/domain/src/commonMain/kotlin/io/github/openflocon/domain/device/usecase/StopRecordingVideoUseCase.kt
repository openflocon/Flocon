package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.StopAdbProcessUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.common.then
import io.github.openflocon.domain.device.models.RecordingDomainModel
import io.github.openflocon.domain.models.ProcessId
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

class StopRecordingVideoUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val stopAdbProcessUseCase: StopAdbProcessUseCase,
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
) {

    suspend operator fun invoke(recording: RecordingDomainModel) : Either<Throwable, String> {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return Failure(Throwable("No device selected"))

        val fileName = "record_${current.packageName}_${System.currentTimeMillis()}.mp4"

        stopAdbProcessUseCase(
            processId = recording.processId,
        )

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
