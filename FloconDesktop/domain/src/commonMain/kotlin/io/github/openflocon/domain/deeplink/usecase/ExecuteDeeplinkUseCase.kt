package io.github.openflocon.domain.deeplink.usecase

import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase

class ExecuteDeeplinkUseCase(
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke(deeplink: String) {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return

        executeAdbCommandUseCase(
            target = AdbCommandTargetDomainModel.Device(current.deviceId),
            command = "shell am start -W -a android.intent.action.VIEW -d \"$deeplink\" ${current.packageName}",
        )
    }
}
