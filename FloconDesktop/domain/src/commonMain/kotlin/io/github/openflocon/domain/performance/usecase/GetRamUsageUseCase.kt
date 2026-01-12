package io.github.openflocon.domain.performance.usecase

import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.common.getOrNull

class GetRamUsageUseCase(
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
) {
    suspend operator fun invoke(deviceSerial: String, packageName: String): String {
        if (packageName.isEmpty()) return "N/A"
        return executeAdbCommandUseCase(
            target = AdbCommandTargetDomainModel.DeviceSerial(deviceSerial),
            command = "shell dumpsys meminfo $packageName"
        ).mapSuccess { output ->
            val pssRegex = Regex("(?:TOTAL PSS:|TOTAL)\\s+(\\d+)", RegexOption.IGNORE_CASE)
            val pssKb = pssRegex.find(output)?.groupValues?.get(1)?.toLongOrNull()
            if (pssKb != null) {
                (pssKb / 1024).toString()
            } else {
                "N/A"
            }
        }.getOrNull() ?: "N/A"
    }
}
