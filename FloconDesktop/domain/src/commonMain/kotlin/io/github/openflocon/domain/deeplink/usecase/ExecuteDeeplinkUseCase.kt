package io.github.openflocon.domain.deeplink.usecase

import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.deeplink.models.DeeplinkDomainModel
import io.github.openflocon.domain.deeplink.repository.DeeplinkRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase

class ExecuteDeeplinkUseCase(
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val addToDeeplinkHistoryUseCase: AddToDeeplinkHistoryUseCase,
    private val deeplinkRepository: DeeplinkRepository,
) {
    suspend operator fun invoke(deeplink: String, deeplinkId: Long) {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return

        // must been done before executing the deeplink, because the new launch overrides the list of deeplinks in the DB
        val originalModel = if(deeplinkId == -1L) {
            null
        } else {
            deeplinkRepository.getDeeplinkById(deeplinkId = deeplinkId, current)
        }

        executeAdbCommandUseCase(
            target = AdbCommandTargetDomainModel.Device(current.deviceId),
            command = "shell am start -W -a android.intent.action.VIEW -d \"$deeplink\" ${current.packageName}",
        ).alsoSuccess {
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
                    )
                )
            }
        }
    }
}
