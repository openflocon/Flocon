package io.github.openflocon.domain.timeline.screenshot

import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.usecase.TakeScreenshotUseCase
import kotlinx.coroutines.withContext
import java.io.File

// takes a screenshot of the app and writes it as a small jpeg
interface TimelineScreenCapturer {
    suspend fun capture(target: DeviceIdAndPackageNameDomainModel, jpegFile: File): Either<Throwable, Unit>
}

internal class TimelineScreenCapturerImpl(
    private val takeScreenshotUseCase: TakeScreenshotUseCase,
    private val dispatcherProvider: DispatcherProvider,
) : TimelineScreenCapturer {

    override suspend fun capture(target: DeviceIdAndPackageNameDomainModel, jpegFile: File): Either<Throwable, Unit> {
        // the png of the device, next to the jpeg: deleted once converted
        val png = File(jpegFile.parentFile, "${jpegFile.nameWithoutExtension}.png")
        return try {
            takeScreenshotUseCase(target = target, outputFile = png).fold(
                doOnFailure = { Failure(it) },
                doOnSuccess = {
                    withContext(dispatcherProvider.data) { convertPngToJpeg(png = png, jpeg = jpegFile) }
                    Success(Unit)
                },
            )
        } catch (t: kotlinx.coroutines.CancellationException) {
            throw t
        } catch (t: Throwable) {
            Failure(t)
        } finally {
            png.delete()
        }
    }
}
