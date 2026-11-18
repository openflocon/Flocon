package io.github.openflocon.domain.versions.usecase

import io.github.openflocon.domain.common.combines
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceFloconSdkVersionNameUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveUpdateAvailableUseCase(
    private val observeLastAvailableFloconVersionUseCase: ObserveLastAvailableFloconVersionUseCase,
    private val observeCurrentDeviceFloconSdkVersionNameUseCase: ObserveCurrentDeviceFloconSdkVersionNameUseCase,
) {
    sealed interface UpdateAvailableDomainModel {
        data class DesktopUpdate(val version: String, val link: String) : UpdateAvailableDomainModel
        data class ClientUpdate(val version: String, val oldVersion: String, val link: String) : UpdateAvailableDomainModel
        data object None : UpdateAvailableDomainModel
    }

    operator fun invoke(desktopAppVersion: String): Flow<UpdateAvailableDomainModel> = combines(
        observeLastAvailableFloconVersionUseCase(),
        observeCurrentDeviceFloconSdkVersionNameUseCase(),
    ).map { (remote, clientLocal) ->
        if (remote == null) {
            UpdateAvailableDomainModel.None
        } else if (isRemoteVersionNewer(localVersion = desktopAppVersion, remoteVersion = remote)) {
            UpdateAvailableDomainModel.DesktopUpdate(
                version = remote,
                link = "https://github.com/openflocon/Flocon/releases/tag/$remote",
            )
        } else if (clientLocal != null && isRemoteVersionNewer(localVersion = clientLocal, remoteVersion = remote)) {
            UpdateAvailableDomainModel.ClientUpdate(
                version = remote,
                oldVersion = clientLocal,
                link = "https://github.com/openflocon/Flocon/releases/tag/$remote",
            )
        } else {
            UpdateAvailableDomainModel.None
        }
    }
}
