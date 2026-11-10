package io.github.openflocon.flocondesktop.core.data.settings.datasource.local

import io.github.openflocon.domain.models.settings.AppScaleSetting
import io.github.openflocon.domain.models.settings.ServerPortsDomainModel
import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.flocondesktop.core.data.settings.models.NetworkSettingsLocal
import io.github.openflocon.flocondesktop.core.data.settings.models.TimelineSettingsLocal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

internal interface SettingsDataSource {
    var networkSettings: NetworkSettingsLocal
    val networkSettingsFlow: StateFlow<NetworkSettingsLocal>

    fun getAdbPath(): String?

    suspend fun setAdbPath(path: String)
    suspend fun setAppScale(value: AppScaleSetting)
    suspend fun setTheme(value: ThemeSetting)

    fun getFloconDocumentsPath(): String?
    suspend fun setFloconDocumentsPath(path: String)

    fun getDismissedDesktopVersion(): String?
    suspend fun setDismissedDesktopVersion(version: String)

    fun getDismissedClientVersion(): String?
    suspend fun setDismissedClientVersion(version: String)

    val pinnedMenuItems: Flow<List<String>>
    val collapsedMenuSections: Flow<Set<String>>
    val startScreen: Flow<String?>
    suspend fun setStartScreen(screenId: String?)
    val hiddenMenuItems: Flow<Set<String>>
    suspend fun setHiddenMenuItems(items: Set<String>)
    suspend fun setPinnedMenuItems(items: List<String>)
    suspend fun setCollapsedMenuSections(sections: Set<String>)

    fun getLastSeenReleaseNotesVersion(): String?
    suspend fun setLastSeenReleaseNotesVersion(version: String)

    val autoPurgeDays: Flow<Int?>
    suspend fun setAutoPurgeDays(days: Int)

    val autoMediaCleanupDays: Flow<Int?>
    suspend fun setAutoMediaCleanupDays(days: Int)

    val mascotEnabled: Flow<Boolean>
    suspend fun setMascotEnabled(enabled: Boolean)

    val transactionsExpandedByDefault: Flow<Boolean>
    suspend fun setTransactionsExpandedByDefault(expanded: Boolean)

    val timelineSettings: Flow<TimelineSettingsLocal>

    // the settings are one stored object: the changes are applied one after the other
    suspend fun updateTimelineSettings(transform: (TimelineSettingsLocal) -> TimelineSettingsLocal)

    val serverPorts: Flow<ServerPortsDomainModel>
    suspend fun setServerPorts(ports: ServerPortsDomainModel)

    val floconDocumentsPath: Flow<String?>
    val adbPath: Flow<String?>
    val appScale: StateFlow<AppScaleSetting>
    val theme: StateFlow<ThemeSetting>
    val dismissedClientVersionFlow: Flow<String?>
}
