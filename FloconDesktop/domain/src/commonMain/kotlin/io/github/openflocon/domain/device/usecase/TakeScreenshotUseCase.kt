package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.then
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.iossimulator.repository.IosSimulatorRepository
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import java.io.File

class TakeScreenshotUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
    private val getFloconDocumentPathUseCase: GetFloconDocumentPathUseCase,
    private val isCurrentDeviceIosUseCase: IsCurrentDeviceIosUseCase,
    private val iosSimulatorRepository: IosSimulatorRepository,
    private val adbRepository: AdbRepository,
    private val handleNewDeviceUseCase: HandleNewDeviceUseCase,
) {

    // [outputFile]: where to write the png, instead of the Screenshot folder of the user
    suspend operator fun invoke(
        target: DeviceIdAndPackageNameDomainModel? = null,
        outputFile: File? = null,
    ): Either<Throwable, String> {
        val current = target ?: getCurrentDeviceIdAndPackageNameUseCase() ?: return Failure(Throwable("No device selected"))

        val fileName = "screenshot_${current.packageName}_${System.currentTimeMillis()}.png"
        val onDeviceFilePath = "/sdcard/$fileName"

        val filePath = if (outputFile != null) {
            outputFile.parentFile?.mkdirs()
            outputFile.absolutePath
        } else {
            val parentFile = File(getFloconDocumentPathUseCase(), "Screenshot")
            if (!parentFile.exists()) {
                parentFile.mkdirs()
            }
            File(parentFile, fileName).absolutePath
        }

        if (isCurrentDeviceIosUseCase(current)) {
            // only the booted iOS Simulator is supported, see IosSimulatorRepository
            return iosSimulatorRepository.takeScreenshot(filePath = filePath)
                .alsoFailure { it.printStackTrace() }
        }

        // without the adb serial of the device, the commands run on every device connected: with several of them, the
        // screenshots of all of them are pulled to the same file and the last one wins
        if (isCaptureAmbiguous(adbRepository.getAdbSerial(current.deviceId), connectedDevices())) {
            // asks the app again: its answer gives the serial
            handleNewDeviceUseCase(current)
            return Failure(Throwable("Several devices are connected and the adb serial of ${current.deviceId} is not known yet"))
        }

        val device = AdbCommandTargetDomainModel.Device(current.deviceId)
        return try {
            executeAdbCommandUseCase(
                target = device,
                command = "shell screencap -p $onDeviceFilePath",
            ).then {
                executeAdbCommandUseCase(
                    target = device,
                    command = "pull $onDeviceFilePath $filePath",
                )
            }.mapSuccess {
                filePath
            }.alsoFailure { it.printStackTrace() }
        } finally {
            // even when cancelled in the middle (the timeline stops a capture when the device or the interval changes):
            // the screenshot must not stay on the device
            withContext(NonCancellable) {
                executeAdbCommandUseCase(
                    target = device,
                    command = "shell rm $onDeviceFilePath",
                ).alsoFailure { it.printStackTrace() }
            }
        }
    }

    private suspend fun connectedDevices(): Int = executeAdbCommandUseCase(
        target = AdbCommandTargetDomainModel.AllDevices,
        command = "devices",
    ).fold(
        doOnFailure = { 0 },
        doOnSuccess = ::countConnectedDevices,
    )
}

/** The devices `adb devices` lists as ready: an offline or unauthorized one is not counted. */
internal fun countConnectedDevices(devicesOutput: String): Int = devicesOutput.lines().count { line ->
    line.trimEnd().endsWith("\tdevice")
}

// the screenshot cannot tell which device it is the one of
internal fun isCaptureAmbiguous(serial: String?, connectedDevices: Int): Boolean = serial == null && connectedDevices > 1
