package io.github.openflocon.domain.versions.usecase

import io.github.openflocon.domain.common.combines
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceFloconSdkUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceFloconSdkVersionNameUseCase
import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.domain.versions.model.FloconSdk
import io.github.openflocon.domain.versions.model.IsLastVersionDomainModel
import io.github.openflocon.domain.versions.repository.VersionsCheckerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/** Compares the SDK of the selected app with the latest release of that SDK (Kotlin, Swift and Flutter are released apart). */
class ObserveIsClientOnLastVersionUseCase(
    private val observeCurrentDeviceFloconSdkUseCase: ObserveCurrentDeviceFloconSdkUseCase,
    private val observeCurrentDeviceFloconSdkVersionNameUseCase: ObserveCurrentDeviceFloconSdkVersionNameUseCase,
    private val versionsCheckerRepository: VersionsCheckerRepository,
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): Flow<IsLastVersionDomainModel> = combines(
        observeLastSdkVersion(),
        observeCurrentDeviceFloconSdkVersionNameUseCase(),
        settingsRepository.dismissedClientVersionFlow,
    ).map { (last, local, dismissed) ->
        if (local == null || last == null) {
            IsLastVersionDomainModel.RunningLastVersion
        } else {
            val (sdk, remote) = last
            val isNewerThanLocal = isRemoteVersionNewer(localVersion = local, remoteVersion = remote)
            val isNewerThanDismissed = dismissed == null || isRemoteVersionNewer(localVersion = dismissed, remoteVersion = remote)
            if (isNewerThanLocal && isNewerThanDismissed) {
                IsLastVersionDomainModel.NewVersionAvailable(
                    name = remote,
                    link = sdk.latestReleaseLink,
                    oldVersion = local,
                    sdk = sdk,
                )
            } else {
                IsLastVersionDomainModel.RunningLastVersion
            }
        }
    }

    /** The latest version of the SDK of the selected app, fetched the first time this SDK is met. */
    private fun observeLastSdkVersion(): Flow<Pair<FloconSdk, String>?> = observeCurrentDeviceFloconSdkUseCase()
        .flatMapLatest { sdk ->
            if (sdk == null) {
                flowOf(null)
            } else {
                versionsCheckerRepository.observeLastSdkVersion(sdk)
                    .onStart { versionsCheckerRepository.checkLastSdkVersion(sdk) }
                    .map { version -> version?.let { sdk to it } }
            }
        }
}
