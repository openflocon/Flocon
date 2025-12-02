package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.success
import io.github.openflocon.domain.device.models.RecordingDomainModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async

class StartRecordingVideoUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
    private val applicationScope: CoroutineScope,
) {

    suspend operator fun invoke(): Either<Throwable, RecordingDomainModel> {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return Failure(Throwable("No device selected"))

        val fileName = "record_${current.packageName}_${System.currentTimeMillis()}.mp4"
        val onDeviceFilePath = "/sdcard/$fileName"

        val processName = "screenrecord"

        val screenRecordAsync = applicationScope.async {
            executeAdbCommandUseCase(
                target = AdbCommandTargetDomainModel.Device(current.deviceId),
                command = "shell $processName $onDeviceFilePath &",
            )
        }

        return RecordingDomainModel(
            onDeviceFilePath = onDeviceFilePath,
            processName = processName,
            completableDeferred = screenRecordAsync,
        ).success()
    }
}
