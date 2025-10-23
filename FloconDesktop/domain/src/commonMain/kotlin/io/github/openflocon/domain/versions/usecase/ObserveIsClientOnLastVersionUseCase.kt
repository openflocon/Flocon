package io.github.openflocon.domain.versions.usecase

import io.github.openflocon.domain.common.combines
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceFloconSdkVersionNameUseCase
import io.github.openflocon.domain.versions.model.IsLastVersionDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveIsClientOnLastVersionUseCase(
    private val observeLastAvailableFloconVersionUseCase: ObserveLastAvailableFloconVersionUseCase,
    private val observeCurrentDeviceFloconSdkVersionNameUseCase: ObserveCurrentDeviceFloconSdkVersionNameUseCase,
) {
    operator fun invoke(): Flow<IsLastVersionDomainModel> {
        return combines(
            observeLastAvailableFloconVersionUseCase(),
            observeCurrentDeviceFloconSdkVersionNameUseCase(),
        ).map { (remote, local) ->
            if (local == null || remote == null)
                IsLastVersionDomainModel.RunningLastVersion
            else {
                val newVersionAvailable =
                    isRemoteVersionNewer(localVersion = local, remoteVersion = remote)
                if (newVersionAvailable) {
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
}
