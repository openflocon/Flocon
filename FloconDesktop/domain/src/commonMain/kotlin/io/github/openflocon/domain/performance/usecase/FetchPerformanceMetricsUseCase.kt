package io.github.openflocon.domain.performance.usecase

import io.github.openflocon.domain.performance.model.PerformanceMetricsDomainModel

class FetchPerformanceMetricsUseCase(
    private val getRamUsageUseCase: GetRamUsageUseCase,
    private val getBatteryLevelUseCase: GetBatteryLevelUseCase,
    private val capturePerformanceScreenshotUseCase: CapturePerformanceScreenshotUseCase,
    private val getFpsUseCase: GetFpsUseCase,
) {
    suspend operator fun invoke(
        deviceSerial: String,
        packageName: String,
        lastFrameCount: Int?,
        lastFetchTime: Long?,
        refreshRate: Double,
    ): PerformanceMetricsDomainModel {
        val currentTime = System.currentTimeMillis()

        val ramInfo = getRamUsageUseCase(deviceSerial, packageName)
        val batteryInfo = getBatteryLevelUseCase(deviceSerial)
        val screenshotPath = capturePerformanceScreenshotUseCase(deviceSerial, packageName)
        val fpsResult = getFpsUseCase(
            deviceSerial = deviceSerial,
            packageName = packageName,
            lastFrameCount = lastFrameCount,
            lastFetchTime = lastFetchTime,
            currentTime = currentTime,
            refreshRate = refreshRate
        )

        return PerformanceMetricsDomainModel(
            ramMb = ramInfo,
            fps = fpsResult.fps,
            jankPercentage = fpsResult.jankPercentage,
            battery = batteryInfo,
            screenshotPath = screenshotPath,
            totalFrames = fpsResult.totalFrames,
            timestamp = currentTime
        )
    }
}
