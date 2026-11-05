package io.github.openflocon.flocondesktop.core.data.settings

import io.github.openflocon.domain.models.settings.AppScaleSetting
import io.github.openflocon.domain.models.settings.ServerPortsDomainModel
import io.github.openflocon.domain.models.settings.ServerPortsOverrides
import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.flocondesktop.core.data.settings.datasource.local.SettingsDataSource
import io.github.openflocon.flocondesktop.core.data.settings.datasource.local.SettingsDataSourcePrefs
import io.github.openflocon.flocondesktop.core.data.settings.models.NetworkSettingsLocal
import io.github.openflocon.flocondesktop.core.data.settings.models.TimelineSettingsLocal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsRepositoryServerPortsTest {

    private val dataSource = FakeSettingsDataSource(ServerPortsDomainModel(websocketPort = 9300, httpPort = 9301))

    private fun repository(overrides: ServerPortsOverrides) = SettingsRepositoryImpl(dataSource, overrides)

    @Test
    fun `without override the saved ports are emitted and saved`() = runBlocking {
        val repository = repository(ServerPortsOverrides.None)
        assertEquals(ServerPortsDomainModel(9300, 9301), repository.serverPorts.first())

        repository.setServerPorts(ServerPortsDomainModel(9400, 9401))

        assertEquals(ServerPortsDomainModel(9400, 9401), dataSource.saved.value)
        assertEquals(ServerPortsDomainModel(9400, 9401), repository.serverPorts.first())
    }

    @Test
    fun `an override is merged with the saved ports`() = runBlocking {
        val repository = repository(ServerPortsOverrides(websocketPort = 9100))
        assertEquals(ServerPortsDomainModel(9100, 9301), repository.serverPorts.first())
    }

    @Test
    fun `an overridden port is never persisted`() = runBlocking {
        val repository = repository(ServerPortsOverrides(websocketPort = 9100))

        repository.setServerPorts(ServerPortsDomainModel(websocketPort = 9100, httpPort = 9401))

        // the websocket port stays the saved one, the not overridden http port is saved
        assertEquals(ServerPortsDomainModel(9300, 9401), dataSource.saved.value)
        assertEquals(ServerPortsDomainModel(9100, 9401), repository.serverPorts.first())
    }

    @Test
    fun `nothing is persisted when both ports are overridden`() = runBlocking {
        val repository = repository(ServerPortsOverrides(websocketPort = 9100, httpPort = 9101))

        repository.setServerPorts(ServerPortsDomainModel(9500, 9501))

        assertEquals(ServerPortsDomainModel(9300, 9301), dataSource.saved.value)
        assertEquals(ServerPortsDomainModel(9100, 9101), repository.serverPorts.first())
    }

    @Test
    fun `saved ports are parsed with the defaults as fallback`() {
        assertEquals(ServerPortsDomainModel(9300, 9301), SettingsDataSourcePrefs.parseServerPorts("9300:9301"))
        assertEquals(ServerPortsDomainModel(), SettingsDataSourcePrefs.parseServerPorts(null))
        assertEquals(ServerPortsDomainModel(), SettingsDataSourcePrefs.parseServerPorts("garbage"))
    }
}

private class FakeSettingsDataSource(initial: ServerPortsDomainModel) : SettingsDataSource {
    val saved = MutableStateFlow(initial)
    override val serverPorts: Flow<ServerPortsDomainModel> = saved
    override suspend fun setServerPorts(ports: ServerPortsDomainModel) {
        saved.value = ports
    }

    override var networkSettings: NetworkSettingsLocal
        get() = TODO("not used")
        set(_) = TODO("not used")
    override val networkSettingsFlow: StateFlow<NetworkSettingsLocal> = MutableStateFlow(NetworkSettingsLocal())
    override fun getAdbPath(): String? = null
    override suspend fun setAdbPath(path: String) = Unit
    override suspend fun setAppScale(value: AppScaleSetting) = Unit
    override suspend fun setTheme(value: ThemeSetting) = Unit
    override fun getFloconDocumentsPath(): String? = null
    override suspend fun setFloconDocumentsPath(path: String) = Unit
    override fun getDismissedDesktopVersion(): String? = null
    override suspend fun setDismissedDesktopVersion(version: String) = Unit
    override fun getDismissedClientVersion(): String? = null
    override suspend fun setDismissedClientVersion(version: String) = Unit
    override val pinnedMenuItems: Flow<List<String>> = emptyFlow()
    override val collapsedMenuSections: Flow<Set<String>> = emptyFlow()
    override val startScreen: Flow<String?> = emptyFlow()
    override suspend fun setStartScreen(screenId: String?) = Unit
    override val hiddenMenuItems: Flow<Set<String>> = emptyFlow()
    override suspend fun setHiddenMenuItems(items: Set<String>) = Unit
    override suspend fun setPinnedMenuItems(items: List<String>) = Unit
    override suspend fun setCollapsedMenuSections(sections: Set<String>) = Unit
    override fun getLastSeenReleaseNotesVersion(): String? = null
    override suspend fun setLastSeenReleaseNotesVersion(version: String) = Unit
    override val mascotEnabled: Flow<Boolean> = emptyFlow()
    override suspend fun setMascotEnabled(enabled: Boolean) = Unit
    override val timelineSettings: Flow<TimelineSettingsLocal> = emptyFlow()
    override suspend fun updateTimelineSettings(transform: (TimelineSettingsLocal) -> TimelineSettingsLocal) = Unit
    override val floconDocumentsPath: Flow<String?> = emptyFlow()
    override val adbPath: Flow<String?> = emptyFlow()
    override val appScale: StateFlow<AppScaleSetting> = MutableStateFlow(AppScaleSetting.DEFAULT)
    override val theme: StateFlow<ThemeSetting> = MutableStateFlow(ThemeSetting.DEFAULT)
    override val dismissedClientVersionFlow: Flow<String?> = emptyFlow()
}
