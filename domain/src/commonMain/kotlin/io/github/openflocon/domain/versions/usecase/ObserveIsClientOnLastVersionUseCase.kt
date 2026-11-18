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
        observeLastClientSdkVersion(),
        observeCurrentDeviceFloconSdkVersionNameUseCase(),
    ).flatMapLatest { (last, local) ->
        if (local == null || last == null) {
            flowOf(IsLastVersionDomainModel.RunningLastVersion)
        } else {
            val (sdk, remote) = last
            // only SDKs with a repository are observed
            val link = sdk.latestReleaseLink ?: return@flatMapLatest flowOf(IsLastVersionDomainModel.RunningLastVersion)
            // the dismissed version is the one of this SDK: the versions of the SDKs can't be compared with each other
            settingsRepository.dismissedClientVersionFlow(sdk).map { dismissed ->
                val isNewerThanLocal = isRemoteVersionNewer(localVersion = local, remoteVersion = remote)
                val isNewerThanDismissed = dismissed == null || isRemoteVersionNewer(localVersion = dismissed, remoteVersion = remote)
                if (isNewerThanLocal && isNewerThanDismissed) {
                    IsLastVersionDomainModel.NewVersionAvailable(
                        name = remote,
                        link = link,
                        oldVersion = local,
                        sdk = sdk,
                    )
                } else {
                    IsLastVersionDomainModel.RunningLastVersion
                }
            }
        }
    }

    /** The latest version of the SDK of the selected app, fetched the first time this SDK is met. */
    private fun observeLastClientSdkVersion(): Flow<Pair<FloconSdk, String>?> = observeCurrentDeviceFloconSdkUseCase()
        .flatMapLatest { sdk ->
            if (sdk == null || sdk.repository == null) {
                flowOf(null)
            } else {
                versionsCheckerRepository.observeLastClientSdkVersion(sdk)
                    .onStart { versionsCheckerRepository.checkLastClientSdkVersion(sdk) }
                    .map { version -> version?.let { sdk to it } }
            }
        }
}
