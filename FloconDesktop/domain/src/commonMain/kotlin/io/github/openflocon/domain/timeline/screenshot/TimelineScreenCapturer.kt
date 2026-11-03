package io.github.openflocon.domain.timeline.screenshot

import io.github.openflocon.domain.adb.isWirelessAdbSerial
import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.usecase.IsCurrentDeviceIosUseCase
import io.github.openflocon.domain.device.usecase.TakeScreenshotUseCase
import kotlinx.coroutines.withContext
import java.io.File

// takes a screenshot of the app and writes it as a small jpeg
interface TimelineScreenCapturer {
    // false for a device the feature does not work with: an Android connected over Wi-Fi (too slow, and it would flood it)
    suspend fun isSupported(target: DeviceIdAndPackageNameDomainModel): Boolean

    suspend fun capture(target: DeviceIdAndPackageNameDomainModel, jpegFile: File): Either<Throwable, Unit>
}

internal class TimelineScreenCapturerImpl(
    private val takeScreenshotUseCase: TakeScreenshotUseCase,
    private val adbRepository: AdbRepository,
    private val isCurrentDeviceIosUseCase: IsCurrentDeviceIosUseCase,
    private val dispatcherProvider: DispatcherProvider,
) : TimelineScreenCapturer {

    // a serial not known yet (the device just connected) does not say: the capture goes on
    override suspend fun isSupported(target: DeviceIdAndPackageNameDomainModel): Boolean {
        if (isCurrentDeviceIosUseCase(target)) return true
        val serial = adbRepository.getAdbSerial(target.deviceId) ?: return true
        return !isWirelessAdbSerial(serial)
    }

    override suspend fun capture(target: DeviceIdAndPackageNameDomainModel, jpegFile: File): Either<Throwable, Unit> {
        // the png of the device, next to the jpeg: deleted once converted
        val png = File(jpegFile.parentFile, "${jpegFile.nameWithoutExtension}.png")
        var success = false
        return try {
            takeScreenshotUseCase(target = target, outputFile = png).fold(
                doOnFailure = { Failure(it) },
                doOnSuccess = {
                    withContext(dispatcherProvider.data) { convertPngToJpeg(png = png, jpeg = jpegFile) }
                    success = true
                    Success(Unit)
                },
            )
        } catch (t: kotlinx.coroutines.CancellationException) {
            throw t
        } catch (t: Throwable) {
            Failure(t)
        } finally {
            png.delete()
            // a jpeg of a capture that failed or was cancelled is not in the index: it would stay until the next launch
            if (!success) jpegFile.delete()
        }
    }
}
