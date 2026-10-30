package io.github.openflocon.domain.accessibility.usecase

import io.github.openflocon.domain.accessibility.repository.AccessibilityRepository
import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase

/**
 * Asks the app of the current device to turn TalkBack on or off.
 * Android only lets an app do it with the WRITE_SECURE_SETTINGS permission, which only adb can grant: it is granted
 * the first time, when the device says it cannot toggle TalkBack yet.
 */
class SetTalkBackEnabledUseCase(
    private val accessibilityRepository: AccessibilityRepository,
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke(enabled: Boolean): Either<Throwable, Unit> {
        val current = getCurrentDeviceIdAndPackageNameUseCase()
            ?: return Failure(Throwable("No device selected"))

        return toggleTalkBack(
            canToggle = accessibilityRepository.getState(current)?.canToggleTalkBack == true,
            grantPermission = {
                executeAdbCommandUseCase(
                    target = AdbCommandTargetDomainModel.Device(current.deviceId),
                    command = "shell pm grant ${current.packageName} $WRITE_SECURE_SETTINGS",
                )
            },
            send = { accessibilityRepository.setTalkBackEnabled(current, enabled) },
        )
    }
}

private const val WRITE_SECURE_SETTINGS = "android.permission.WRITE_SECURE_SETTINGS"

/** Grants the permission first when the device cannot toggle TalkBack yet, and sends nothing if that fails. */
internal suspend fun toggleTalkBack(
    canToggle: Boolean,
    grantPermission: suspend () -> Either<Throwable, *>,
    send: suspend () -> Unit,
): Either<Throwable, Unit> {
    if (!canToggle) {
        val granted = grantPermission()
        if (granted is Failure) return Failure(granted.value)
    }
    send()
    return Success(Unit)
}
