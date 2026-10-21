package io.github.openflocon.domain.deeplink.usecase

import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.deeplink.models.DeeplinkDomainModel
import io.github.openflocon.domain.deeplink.repository.DeeplinkRepository
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.models.isPlatformIos
import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.iossimulator.repository.IosSimulatorRepository

class ExecuteDeeplinkUseCase(
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val addToDeeplinkHistoryUseCase: AddToDeeplinkHistoryUseCase,
    private val deeplinkRepository: DeeplinkRepository,
    private val devicesRepository: DevicesRepository,
    private val iosSimulatorRepository: IosSimulatorRepository,
) {
    suspend operator fun invoke(deeplink: String, deeplinkId: Long, saveIntoHistory: Boolean,
        target: DeviceIdAndPackageNameDomainModel? = null,
    ): Either<Throwable, String> {
        val current = target ?: getCurrentDeviceIdAndPackageNameUseCase() ?: return Failure(IllegalStateException("No device"))

        // must been done before executing the deeplink, because the new launch overrides the list of deeplinks in the DB
        val originalModel = if (deeplinkId == -1L) {
            null
        } else {
            deeplinkRepository.getDeeplinkById(deeplinkId = deeplinkId, current)
        }

        return open(deeplink = deeplink, current = current)
            .alsoSuccess {
                if (saveIntoHistory) {
                    originalModel?.let { model ->
                        // from an existing deeplink
                        addToDeeplinkHistoryUseCase(
                            item = model.copy(
                                link = deeplink,
                            )
                        )
                    } ?: run {
                        // from freeform
                        addToDeeplinkHistoryUseCase(
                            item = DeeplinkDomainModel(
                                link = deeplink,
                                label = null,
                                description = null,
                                id = 0, // will be created by the DB
                                parameters = emptyList(),
                            )
                        )
                    }
                }
            }
    }

    private suspend fun open(deeplink: String, current: DeviceIdAndPackageNameDomainModel): Either<Throwable, String> {
        val isIos = devicesRepository.getCurrentDevice()
            ?.takeIf { it.deviceId == current.deviceId }
            ?.isPlatformIos() == true

        return if (isIos) {
            // only the booted iOS Simulator is supported, see IosSimulatorRepository
            iosSimulatorRepository.openUrl(url = deeplink)
        } else {
            executeAdbCommandUseCase(
                target = AdbCommandTargetDomainModel.Device(current.deviceId),
                command = "shell am start -W -a android.intent.action.VIEW -d \"$deeplink\" ${current.packageName}",
            )
        }
    }
}
