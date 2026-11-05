package io.github.openflocon.domain.timeline.screenshot

import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.models.canTakeScreenshots
import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.device.usecase.TakeScreenshotUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File

// takes a screenshot of the app and writes it as a small jpeg
interface TimelineScreenCapturer {
    // false for a device that cannot take them: an Android connected over Wi-Fi has no adb link
    suspend fun isSupported(target: DeviceIdAndPackageNameDomainModel): Boolean

    suspend fun capture(target: DeviceIdAndPackageNameDomainModel, jpegFile: File): Either<Throwable, Unit>
}

internal class TimelineScreenCapturerImpl(
    private val takeScreenshotUseCase: TakeScreenshotUseCase,
    private val devicesRepository: DevicesRepository,
    private val dispatcherProvider: DispatcherProvider,
) : TimelineScreenCapturer {

    // a device not known yet does not say: the capture goes on
    override suspend fun isSupported(target: DeviceIdAndPackageNameDomainModel): Boolean = devicesRepository.devices.first()
        .firstOrNull { it.deviceId == target.deviceId }
        ?.canTakeScreenshots() ?: true

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
