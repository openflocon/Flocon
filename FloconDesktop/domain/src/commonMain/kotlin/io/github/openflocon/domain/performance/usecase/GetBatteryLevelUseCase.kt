package io.github.openflocon.domain.performance.usecase

import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.common.getOrNull

class GetBatteryLevelUseCase(
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
) {
    suspend operator fun invoke(deviceSerial: String): String {
        return executeAdbCommandUseCase(
            target = AdbCommandTargetDomainModel.DeviceSerial(deviceSerial),
            command = "shell dumpsys battery"
        ).mapSuccess { output ->
            val level = output.lines().find { it.contains("level:") }?.substringAfter("level:")?.trim() ?: "N/A"
            "$level%"
        }.getOrNull() ?: "N/A"
    }
}
