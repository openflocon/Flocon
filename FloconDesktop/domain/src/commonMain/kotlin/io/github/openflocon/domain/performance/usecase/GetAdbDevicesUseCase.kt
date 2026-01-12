package io.github.openflocon.domain.performance.usecase

import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.common.getOrNull

class GetAdbDevicesUseCase(
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
) {
    suspend operator fun invoke(): List<String> {
        return executeAdbCommandUseCase(AdbCommandTargetDomainModel.AllDevices, "devices")
            .mapSuccess { output ->
                output.lines()
                    .drop(1) // Skip "List of devices attached"
                    .mapNotNull { line ->
                        val parts = line.split("\t")
                        if (parts.size >= 2 && parts[1] == "device") {
                            parts[0]
                        } else {
                            null
                        }
                    }
            }.getOrNull() ?: emptyList()
    }
}
