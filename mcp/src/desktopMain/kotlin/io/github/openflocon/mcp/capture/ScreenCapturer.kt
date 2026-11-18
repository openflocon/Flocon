package io.github.openflocon.mcp.capture

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.models.RecordingDomainModel
import io.github.openflocon.domain.device.usecase.StartRecordingVideoUseCase
import io.github.openflocon.domain.device.usecase.StopRecordingVideoUseCase
import io.github.openflocon.domain.device.usecase.TakeScreenshotUseCase

/**
 * The actions of the capture tools on a device (adb on Android, the booted Simulator on iOS)
 */
internal interface ScreenCapturer {
    /** @return the path of the saved PNG */
    suspend fun takeScreenshot(target: DeviceIdAndPackageNameDomainModel): Either<Throwable, String>

    suspend fun startRecording(target: DeviceIdAndPackageNameDomainModel): Either<Throwable, RecordingDomainModel>

    /** @return the path of the saved mp4 */
    suspend fun stopRecording(recording: RecordingDomainModel): Either<Throwable, String>
}

internal class UseCaseScreenCapturer(
    private val takeScreenshotUseCase: TakeScreenshotUseCase,
    private val startRecordingVideoUseCase: StartRecordingVideoUseCase,
    private val stopRecordingVideoUseCase: StopRecordingVideoUseCase,
) : ScreenCapturer {
    override suspend fun takeScreenshot(target: DeviceIdAndPackageNameDomainModel) = takeScreenshotUseCase(target)

    override suspend fun startRecording(target: DeviceIdAndPackageNameDomainModel) = startRecordingVideoUseCase(target)

    override suspend fun stopRecording(recording: RecordingDomainModel) = stopRecordingVideoUseCase(recording)
}
