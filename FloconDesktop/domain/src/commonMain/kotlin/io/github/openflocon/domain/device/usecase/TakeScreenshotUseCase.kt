package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.then
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.iossimulator.repository.IosSimulatorRepository
import java.io.File

class TakeScreenshotUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
    private val getFloconDocumentPathUseCase: GetFloconDocumentPathUseCase,
    private val isCurrentDeviceIosUseCase: IsCurrentDeviceIosUseCase,
    private val iosSimulatorRepository: IosSimulatorRepository,
) {

    suspend operator fun invoke(target: DeviceIdAndPackageNameDomainModel? = null): Either<Throwable, String> {
        val current = target ?: getCurrentDeviceIdAndPackageNameUseCase() ?: return Failure(Throwable("No device selected"))

        val fileName = "screenshot_${current.packageName}_${System.currentTimeMillis()}.png"
        val onDeviceFilePath = "/sdcard/$fileName"

        val parentFile = File(getFloconDocumentPathUseCase(), "Screenshot")
        if (!parentFile.exists()) {
            parentFile.mkdirs()
        }

        val filePath = File(parentFile, fileName).absolutePath

        if (isCurrentDeviceIosUseCase(current)) {
            // only the booted iOS Simulator is supported, see IosSimulatorRepository
            return iosSimulatorRepository.takeScreenshot(filePath = filePath)
                .alsoFailure { it.printStackTrace() }
        }

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
