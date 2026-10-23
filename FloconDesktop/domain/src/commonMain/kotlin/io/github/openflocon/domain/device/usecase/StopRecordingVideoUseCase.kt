package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.adb.usecase.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.device.models.RecordingDomainModel
import io.github.openflocon.domain.iossimulator.repository.IosSimulatorRepository
import java.io.File

class StopRecordingVideoUseCase(
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
    private val getFloconDocumentPathUseCase: GetFloconDocumentPathUseCase,
    private val iosSimulatorRepository: IosSimulatorRepository,
) {

    suspend operator fun invoke(recording: RecordingDomainModel): Either<Throwable, String> = when (recording) {
        // the simulator records on this computer, it does not depend on the selected device
        is RecordingDomainModel.IosSimulator -> iosSimulatorRepository.stopRecording(filePath = recording.filePath)

        is RecordingDomainModel.Android -> stopAndroidRecording(recording)
    }

    private suspend fun stopAndroidRecording(recording: RecordingDomainModel.Android): Either<Throwable, String> {
        // the device the recording was started on, not the currently selected one
        val fileName = "record_${recording.packageName}_${System.currentTimeMillis()}.mp4"

        executeAdbCommandUseCase(
            target = AdbCommandTargetDomainModel.Device(recording.deviceId),
            command = "shell pkill -l 2 ${recording.processName}",
        )

        // wait until the record is done
        recording.completableDeferred.await()

        val parentFile = File(getFloconDocumentPathUseCase(), "ScreenRecord")
        if (!parentFile.exists()) {
            parentFile.mkdirs()
        }

        val filePath = File(parentFile, fileName).absolutePath

        executeAdbCommandUseCase(
            target = AdbCommandTargetDomainModel.Device(recording.deviceId),
            command = "pull ${recording.onDeviceFilePath} $filePath",
        )

        executeAdbCommandUseCase(
            target = AdbCommandTargetDomainModel.Device(recording.deviceId),
            command = "shell rm ${recording.onDeviceFilePath}",
        )

        return Success(filePath)
    }
}
