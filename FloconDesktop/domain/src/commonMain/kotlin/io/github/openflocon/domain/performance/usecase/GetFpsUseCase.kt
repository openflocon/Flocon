package io.github.openflocon.domain.performance.usecase

import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.common.getOrNull

data class FpsResult(
    val fps: Double,
    val jankPercentage: Double,
    val totalFrames: Int?,
)

class GetFpsUseCase(
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
) {
    suspend operator fun invoke(
        deviceSerial: String,
        packageName: String,
        lastFrameCount: Int?,
        lastFetchTime: Long?,
        currentTime: Long,
        refreshRate: Double,
    ): FpsResult {
        if (packageName.isEmpty()) return FpsResult(0.0, 0.0, null)

        var currentTotalFrames: Int? = null
        var jankPercent = 0.0

        val calculatedFps = executeAdbCommandUseCase(
            target = AdbCommandTargetDomainModel.DeviceSerial(deviceSerial),
            command = "shell dumpsys gfxinfo $packageName"
        ).mapSuccess { output ->
            val totalFramesRegex = Regex("Total frames rendered:\\s*(\\d+)")
            val jankyFramesRegex = Regex("Janky frames:\\s*(\\d+)\\s*\\(([^%]+)%\\)")

            val totalFrames = totalFramesRegex.find(output)?.groupValues?.get(1)?.toIntOrNull()
            val jankMatch = jankyFramesRegex.find(output)
            jankPercent = jankMatch?.groupValues?.get(2)?.toDoubleOrNull() ?: 0.0

            currentTotalFrames = totalFrames

            if (totalFrames != null && lastFrameCount != null && lastFetchTime != null) {
                val frameDelta = totalFrames - lastFrameCount
                val timeDeltaSeconds = (currentTime - lastFetchTime) / 1000.0
                if (timeDeltaSeconds > 0) {
                    if (frameDelta > 0) {
                        (frameDelta / timeDeltaSeconds).coerceIn(0.0, refreshRate)
                    } else {
                        // If no new frames, assume it's running at refresh rate (Idle)
                        refreshRate
                    }
                } else refreshRate
            } else {
                refreshRate // Default to refresh rate for the first fetch or if data is missing
            }
        }.getOrNull() ?: 0.0

        return FpsResult(
            fps = calculatedFps,
            jankPercentage = jankPercent,
            totalFrames = currentTotalFrames
        )
    }
}
