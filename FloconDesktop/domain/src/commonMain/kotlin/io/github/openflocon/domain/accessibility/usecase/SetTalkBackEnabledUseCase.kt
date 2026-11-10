package io.github.openflocon.domain.accessibility.usecase

import io.github.openflocon.domain.accessibility.repository.AccessibilityRepository
import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.device.models.supportsAdb
import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import kotlinx.coroutines.flow.first

/**
 * Asks the app of the current device to turn TalkBack on or off.
 * Android only lets an app do it with the WRITE_SECURE_SETTINGS permission, which only adb can grant: it is granted
 * the first time, when the device says it cannot toggle TalkBack yet.
 * Over wifi adb is not available: the request is sent anyway, the device re-checks the permission and, when it is
 * missing, opens the TalkBack settings instead.
 */
class SetTalkBackEnabledUseCase(
    private val accessibilityRepository: AccessibilityRepository,
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val devicesRepository: DevicesRepository,
) {
    suspend operator fun invoke(enabled: Boolean): Either<Throwable, SetTalkBackEnabledResult> {
        val current = getCurrentDeviceIdAndPackageNameUseCase()
            ?: return Failure(Throwable("No device selected"))

        val device = devicesRepository.devices.first().firstOrNull { it.deviceId == current.deviceId }

        return toggleTalkBack(
            canToggle = accessibilityRepository.getState(current)?.canToggleTalkBack == true,
            canGrant = device?.supportsAdb() != false,
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

internal const val WRITE_SECURE_SETTINGS = "android.permission.WRITE_SECURE_SETTINGS"

enum class SetTalkBackEnabledResult {
    /** The device was asked to turn TalkBack on or off by itself */
    Sent,

    /** The permission is missing and cannot be granted from here (wifi): the device opens its TalkBack settings */
    SentWithoutPermission,
}

/**
 * Grants the permission first when the device cannot toggle TalkBack yet, and sends nothing if that fails.
 * When it cannot be granted from here ([canGrant] false), the request is sent anyway: the device answers with its real state.
 */
internal suspend fun toggleTalkBack(
    canToggle: Boolean,
    canGrant: Boolean,
    grantPermission: suspend () -> Either<Throwable, *>,
    send: suspend () -> Unit,
): Either<Throwable, SetTalkBackEnabledResult> {
    if (canToggle) {
        send()
        return Success(SetTalkBackEnabledResult.Sent)
    }
    if (!canGrant) {
        send()
        return Success(SetTalkBackEnabledResult.SentWithoutPermission)
    }
    val granted = grantPermission()
    if (granted is Failure) return Failure(granted.value)
    send()
    return Success(SetTalkBackEnabledResult.Sent)
}
