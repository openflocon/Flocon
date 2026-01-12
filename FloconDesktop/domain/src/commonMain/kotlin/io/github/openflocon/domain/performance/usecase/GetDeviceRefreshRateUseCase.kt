package io.github.openflocon.domain.performance.usecase

import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.common.getOrNull

class GetDeviceRefreshRateUseCase(
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
) {
    suspend operator fun invoke(deviceSerial: String): Double {
        return executeAdbCommandUseCase(
            target = AdbCommandTargetDomainModel.DeviceSerial(deviceSerial),
            command = "shell dumpsys display"
        ).mapSuccess { output ->
            val refreshRateRegex = Regex("mRefreshRate=([\\d.]+)")
            refreshRateRegex.find(output)?.groupValues?.get(1)?.toDoubleOrNull() ?: 60.0
        }.getOrNull() ?: 60.0
    }
}
