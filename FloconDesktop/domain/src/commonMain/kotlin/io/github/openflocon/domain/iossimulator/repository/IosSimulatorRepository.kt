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
}
