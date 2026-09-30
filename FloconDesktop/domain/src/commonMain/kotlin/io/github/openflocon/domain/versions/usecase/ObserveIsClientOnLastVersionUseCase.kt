package io.github.openflocon.domain.versions.usecase

import io.github.openflocon.domain.common.combines
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceFloconSdkVersionNameUseCase
import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.domain.versions.model.IsLastVersionDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveIsClientOnLastVersionUseCase(
    private val observeLastAvailableFloconVersionUseCase: ObserveLastAvailableFloconVersionUseCase,
    private val observeCurrentDeviceFloconSdkVersionNameUseCase: ObserveCurrentDeviceFloconSdkVersionNameUseCase,
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): Flow<IsLastVersionDomainModel> = combines(
        observeLastAvailableFloconVersionUseCase(),
        observeCurrentDeviceFloconSdkVersionNameUseCase(),
        settingsRepository.dismissedClientVersionFlow,
    ).map { (remote, local, dismissed) ->
        if (local == null || remote == null) {
            IsLastVersionDomainModel.RunningLastVersion
        } else {
            val isNewerThanLocal = isRemoteVersionNewer(localVersion = local, remoteVersion = remote)
            val isNewerThanDismissed = dismissed == null || isRemoteVersionNewer(localVersion = dismissed, remoteVersion = remote)
            if (isNewerThanLocal && isNewerThanDismissed) {
                IsLastVersionDomainModel.NewVersionAvailable(
                    name = remote,
                    link = "https://github.com/openflocon/Flocon/releases/tag/$remote",
                    oldVersion = local,
                )
            } else {
                IsLastVersionDomainModel.RunningLastVersion
            }
        }
    }
}
