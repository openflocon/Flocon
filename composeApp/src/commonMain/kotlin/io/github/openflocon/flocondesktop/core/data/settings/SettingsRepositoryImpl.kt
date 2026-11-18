package io.github.openflocon.flocondesktop.core.data.settings

import io.github.openflocon.domain.models.settings.AppScaleSetting
import io.github.openflocon.domain.models.settings.NetworkSettings
import io.github.openflocon.domain.models.settings.ServerPortsDomainModel
import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.domain.settings.repository.AdbForwardStatus
import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.domain.versions.model.FloconSdk
import io.github.openflocon.flocondesktop.core.data.settings.datasource.local.SettingsDataSource
import io.github.openflocon.flocondesktop.core.data.settings.models.toDomain
import io.github.openflocon.flocondesktop.core.data.settings.models.toLocal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest

internal class SettingsRepositoryImpl(
    private val localSettingsDataSource: SettingsDataSource,
) : SettingsRepository {

    // TODO: Consider moving all settings into one StateFlow
    override val adbPath: Flow<String?> = localSettingsDataSource.adbPath
    override val appScale: StateFlow<AppScaleSetting> = localSettingsDataSource.appScale
    override val appLanguage: StateFlow<String?> = localSettingsDataSource.appLanguage
    override val theme: StateFlow<ThemeSetting> = localSettingsDataSource.theme

    override val savedServerPorts: Flow<ServerPortsDomainModel> = localSettingsDataSource.serverPorts

    override suspend fun setServerPorts(ports: ServerPortsDomainModel) {
        localSettingsDataSource.setServerPorts(ports)
    }

    private val _adbForwardStatus = MutableStateFlow(AdbForwardStatus.UNKNOWN)
    override val adbForwardStatus: StateFlow<AdbForwardStatus> = _adbForwardStatus

    override fun setAdbForwardStatus(status: AdbForwardStatus) {
        _adbForwardStatus.value = status
    }

    override var networkSettings: NetworkSettings
        get() = localSettingsDataSource.networkSettings.toDomain()
        set(value) {
            localSettingsDataSource.networkSettings = value.toLocal()
        }
    override val networkSettingsFlow: Flow<NetworkSettings> = localSettingsDataSource.networkSettingsFlow
        .mapLatest { it.toDomain() }

    override fun getAdbPath(): String? = localSettingsDataSource.getAdbPath()

    override suspend fun setAdbPath(path: String) {
        localSettingsDataSource.setAdbPath(path)
    }

    override suspend fun setAppScale(value: AppScaleSetting) {
        localSettingsDataSource.setAppScale(value)
    }

    override suspend fun setAppLanguage(languageTag: String?) {
        localSettingsDataSource.setAppLanguage(languageTag)
    }

    override suspend fun setTheme(value: ThemeSetting) {
        localSettingsDataSource.setTheme(value)
    }

    override val floconDocumentsPath: Flow<String?> = localSettingsDataSource.floconDocumentsPath

    override fun getFloconDocumentsPath(): String? = localSettingsDataSource.getFloconDocumentsPath()

    override suspend fun setFloconDocumentsPath(path: String) {
        localSettingsDataSource.setFloconDocumentsPath(path)
    }

    override fun getDismissedDesktopVersion(): String? = localSettingsDataSource.getDismissedDesktopVersion()

    override suspend fun setDismissedDesktopVersion(version: String) {
        localSettingsDataSource.setDismissedDesktopVersion(version)
    }

    override fun dismissedClientVersionFlow(sdk: FloconSdk): Flow<String?> = localSettingsDataSource.dismissedClientVersionFlow(sdk)

    override fun getDismissedClientVersion(sdk: FloconSdk): String? = localSettingsDataSource.getDismissedClientVersion(sdk)

    override suspend fun setDismissedClientVersion(sdk: FloconSdk, version: String) {
        localSettingsDataSource.setDismissedClientVersion(sdk, version)
    }

    override fun getLastSeenReleaseNotesVersion(): String? = localSettingsDataSource.getLastSeenReleaseNotesVersion()

    override suspend fun setLastSeenReleaseNotesVersion(version: String) {
        localSettingsDataSource.setLastSeenReleaseNotesVersion(version)
    }
}
