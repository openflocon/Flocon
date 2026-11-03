package io.github.openflocon.domain.settings

import io.github.openflocon.domain.models.settings.AppScaleSetting
import io.github.openflocon.domain.models.settings.NetworkSettings
import io.github.openflocon.domain.models.settings.ServerPortsDomainModel
import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.domain.settings.repository.AdbForwardStatus
import io.github.openflocon.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow

internal class FakeSettingsRepository : SettingsRepository {
    override var networkSettings: NetworkSettings = NetworkSettings(
        pinnedDetails = false,
        displayOldSessions = false,
        autoScroll = false,
        invertList = false,
    )
    override val networkSettingsFlow: Flow<NetworkSettings> = emptyFlow()
    override fun getAdbPath(): String = "adb"
    override suspend fun setAdbPath(path: String) {}
    override suspend fun setAppScale(value: AppScaleSetting) {}
    override suspend fun setTheme(value: ThemeSetting) {}
    override fun getFloconDocumentsPath(): String? = null
    override suspend fun setFloconDocumentsPath(path: String) {}
    override val floconDocumentsPath: Flow<String?> = emptyFlow()
    override fun getDismissedDesktopVersion(): String? = null
    override suspend fun setDismissedDesktopVersion(version: String) {}
    override val dismissedClientVersionFlow: Flow<String?> = emptyFlow()
    override fun getDismissedClientVersion(): String? = null
    override suspend fun setDismissedClientVersion(version: String) {}
    override fun getLastSeenReleaseNotesVersion(): String? = null
    override suspend fun setLastSeenReleaseNotesVersion(version: String) {}
    override val adbPath: Flow<String?> = emptyFlow()
    val savedServerPorts = MutableStateFlow(ServerPortsDomainModel())
    override val serverPorts: Flow<ServerPortsDomainModel> = savedServerPorts
    override suspend fun setServerPorts(ports: ServerPortsDomainModel) {
        savedServerPorts.value = ports
    }
    override val appScale: StateFlow<AppScaleSetting> = MutableStateFlow(AppScaleSetting.DEFAULT)
    override val theme: StateFlow<ThemeSetting> = MutableStateFlow(ThemeSetting.DEFAULT)
    override val adbForwardStatus: StateFlow<AdbForwardStatus> = MutableStateFlow(AdbForwardStatus.UNKNOWN)
    override fun setAdbForwardStatus(status: AdbForwardStatus) {}
}
