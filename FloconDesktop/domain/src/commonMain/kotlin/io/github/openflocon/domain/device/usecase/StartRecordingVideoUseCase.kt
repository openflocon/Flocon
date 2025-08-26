package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.StartAdbProcessUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.then
import io.github.openflocon.domain.device.models.RecordingDomainModel
import io.github.openflocon.domain.models.ProcessId
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

class StartRecordingVideoUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val startAdbProcessUseCase: StartAdbProcessUseCase,
) {

    suspend operator fun invoke() : Either<Throwable, RecordingDomainModel> {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return Failure(Throwable("No device selected"))

        val fileName = "record_${current.packageName}_${System.currentTimeMillis()}.mp4"
        val onDeviceFilePath = "/sdcard/$fileName"

        return startAdbProcessUseCase(
            target = AdbCommandTargetDomainModel.Device(current.deviceId),
            command = "shell screenrecord $onDeviceFilePath",
        ).mapSuccess { processId ->
            RecordingDomainModel(
                onDeviceFilePath = onDeviceFilePath,
                processId = processId,
            )
        }.alsoFailure { it.printStackTrace() }
    }
}
