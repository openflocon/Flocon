package io.github.openflocon.domain.iossimulator.repository

import io.github.openflocon.domain.common.Either

/**
 * Talks to the iOS Simulator through `xcrun simctl` (macOS with Xcode only).
 *
 * The iOS SDK sends `identifierForVendor` as device id, which is not the simulator UDID,
 * so commands target the `booted` simulator. Physical iOS devices are not supported
 * (they would need `xcrun devicectl device process launch --payload-url`).
 */
interface IosSimulatorRepository {
    suspend fun openUrl(url: String): Either<Throwable, String>

    /**
     * Saves a PNG screenshot of the booted simulator.
     * @return the path of the saved file
     */
    suspend fun takeScreenshot(filePath: String): Either<Throwable, String>

    /**
     * Starts recording the screen of the booted simulator into [filePath],
     * until [stopRecording] is called with the same path.
     */
    suspend fun startRecording(filePath: String): Either<Throwable, Unit>

    /**
     * Stops the recording started with [startRecording], once the video is finalized.
     * @return the path of the saved file
     */
    suspend fun stopRecording(filePath: String): Either<Throwable, String>
}
