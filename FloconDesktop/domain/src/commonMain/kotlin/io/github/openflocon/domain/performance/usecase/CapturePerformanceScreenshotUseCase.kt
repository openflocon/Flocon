package io.github.openflocon.domain.performance.usecase

import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.common.then
import java.io.File
import java.nio.file.Paths

class CapturePerformanceScreenshotUseCase(
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
) {
    suspend operator fun invoke(deviceSerial: String, packageName: String): String? {
        val deviceTarget = AdbCommandTargetDomainModel.DeviceSerial(deviceSerial)
        val timestamp = System.currentTimeMillis()
        val fileName = "perf_screenshot_${packageName}_$timestamp.png"
        val onDeviceFilePath = "/sdcard/$fileName"
        
        val metricsDir = Paths.get(System.getProperty("user.home"), "Desktop", "Flocon", "Metrics").toFile()
        if (!metricsDir.exists()) metricsDir.mkdirs()
        
        val localFilePath = File(metricsDir, fileName).absolutePath
        
        var screenshotPath: String? = null
        executeAdbCommandUseCase(deviceTarget, "shell screencap -p $onDeviceFilePath")
            .then { executeAdbCommandUseCase(deviceTarget, "pull $onDeviceFilePath $localFilePath") }
            .then { executeAdbCommandUseCase(deviceTarget, "shell rm $onDeviceFilePath") }
            .alsoSuccess { screenshotPath = localFilePath }
            
        return screenshotPath
    }
}
