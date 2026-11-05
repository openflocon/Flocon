package io.github.openflocon.flocondesktop.core.data.settings

import io.github.openflocon.domain.models.settings.AppScaleSetting
import io.github.openflocon.domain.models.settings.NetworkSettings
import io.github.openflocon.domain.models.settings.ServerPortsDomainModel
import io.github.openflocon.domain.models.settings.ServerPortsOverrides
import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.domain.settings.repository.AdbForwardStatus
import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.flocondesktop.core.data.settings.datasource.local.SettingsDataSource
import io.github.openflocon.flocondesktop.core.data.settings.models.toDomain
import io.github.openflocon.flocondesktop.core.data.settings.models.toLocal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest

internal class SettingsRepositoryImpl(
    private val localSettingsDataSource: SettingsDataSource,
    private val serverPortsOverrides: ServerPortsOverrides,
) : SettingsRepository {

    // TODO: Consider moving all settings into one StateFlow
    override val adbPath: Flow<String?> = localSettingsDataSource.adbPath
    override val appScale: StateFlow<AppScaleSetting> = localSettingsDataSource.appScale
    override val theme: StateFlow<ThemeSetting> = localSettingsDataSource.theme

    // the launch options win over the saved ports, for this session only
    override val serverPorts: Flow<ServerPortsDomainModel> = localSettingsDataSource.serverPorts
        .map { serverPortsOverrides.applyTo(it) }

    override suspend fun setServerPorts(ports: ServerPortsDomainModel) {
        // an overridden port is never persisted, the saved one is kept
        val saved = localSettingsDataSource.serverPorts.first()
        localSettingsDataSource.setServerPorts(
            ServerPortsDomainModel(
                websocketPort = if (serverPortsOverrides.websocketPort != null) saved.websocketPort else ports.websocketPort,
                httpPort = if (serverPortsOverrides.httpPort != null) saved.httpPort else ports.httpPort,
            ),
        )
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

    override val dismissedClientVersionFlow: Flow<String?> = localSettingsDataSource.dismissedClientVersionFlow

    override fun getDismissedClientVersion(): String? = localSettingsDataSource.getDismissedClientVersion()

    override suspend fun setDismissedClientVersion(version: String) {
        localSettingsDataSource.setDismissedClientVersion(version)
    }

    override fun getLastSeenReleaseNotesVersion(): String? = localSettingsDataSource.getLastSeenReleaseNotesVersion()

    override suspend fun setLastSeenReleaseNotesVersion(version: String) {
        localSettingsDataSource.setLastSeenReleaseNotesVersion(version)
    }
}
