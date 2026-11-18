package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.success
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.models.RecordingDomainModel
import io.github.openflocon.domain.iossimulator.repository.IosSimulatorRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import java.io.File

class StartRecordingVideoUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
    private val getFloconDocumentPathUseCase: GetFloconDocumentPathUseCase,
    private val isCurrentDeviceIosUseCase: IsCurrentDeviceIosUseCase,
    private val iosSimulatorRepository: IosSimulatorRepository,
    private val applicationScope: CoroutineScope,
) {

    suspend operator fun invoke(target: DeviceIdAndPackageNameDomainModel? = null): Either<Throwable, RecordingDomainModel> {
        val current = target ?: getCurrentDeviceIdAndPackageNameUseCase() ?: return Failure(Throwable("No device selected"))

        val fileName = "record_${current.packageName}_${System.currentTimeMillis()}.mp4"

        if (isCurrentDeviceIosUseCase(current)) {
            // only the booted iOS Simulator is supported, see IosSimulatorRepository
            val parentFile = File(getFloconDocumentPathUseCase(), "ScreenRecord")
            if (!parentFile.exists()) {
                parentFile.mkdirs()
            }
            val filePath = File(parentFile, fileName).absolutePath

            return iosSimulatorRepository.startRecording(filePath = filePath)
                .mapSuccess { RecordingDomainModel.IosSimulator(filePath = filePath) }
        }

        val onDeviceFilePath = "/sdcard/$fileName"

        val processName = "screenrecord"

        val screenRecordAsync = applicationScope.async {
            executeAdbCommandUseCase(
                target = AdbCommandTargetDomainModel.Device(current.deviceId),
                command = "shell $processName $onDeviceFilePath &",
            )
        }

        return RecordingDomainModel.Android(
            deviceId = current.deviceId,
            packageName = current.packageName,
            onDeviceFilePath = onDeviceFilePath,
            processName = processName,
            completableDeferred = screenRecordAsync,
        ).success()
    }
}
