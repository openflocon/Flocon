package io.github.openflocon.domain.performance.usecase

import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.common.getOrNull

class GetRamUsageUseCase(
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
) {
    suspend operator fun invoke(deviceSerial: String, packageName: String): Long? {
        if (packageName.isEmpty()) return null
        return executeAdbCommandUseCase(
            target = AdbCommandTargetDomainModel.DeviceSerial(deviceSerial),
            command = "shell dumpsys meminfo $packageName"
        ).mapSuccess { output ->
            val pssRegex = Regex("(?:TOTAL PSS:|TOTAL)\\s+(\\d+)", RegexOption.IGNORE_CASE)
            val pssKb = pssRegex.find(output)?.groupValues?.get(1)?.toLongOrNull()
            pssKb?.let {
                it * 1024
            }
        }.getOrNull()
    }
}
