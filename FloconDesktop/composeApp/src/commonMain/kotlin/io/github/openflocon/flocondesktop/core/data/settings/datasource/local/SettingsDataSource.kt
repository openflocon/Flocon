package io.github.openflocon.flocondesktop.core.data.settings.datasource.local

import io.github.openflocon.domain.models.settings.AppScaleSetting
import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.flocondesktop.core.data.settings.models.NetworkSettingsLocal
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
    suspend fun setPinnedMenuItems(items: List<String>)
    suspend fun setCollapsedMenuSections(sections: Set<String>)

    fun getLastSeenReleaseNotesVersion(): String?
    suspend fun setLastSeenReleaseNotesVersion(version: String)

    val mascotEnabled: Flow<Boolean>
    suspend fun setMascotEnabled(enabled: Boolean)

    val timelineAutoScroll: Flow<Boolean>
    suspend fun setTimelineAutoScroll(autoScroll: Boolean)

    val timelineInvertList: Flow<Boolean>
    suspend fun setTimelineInvertList(invertList: Boolean)

    val timelinePinPanel: Flow<Boolean>
    suspend fun setTimelinePinPanel(pinPanel: Boolean)

    val floconDocumentsPath: Flow<String?>
    val adbPath: Flow<String?>
    val appScale: StateFlow<AppScaleSetting>
    val theme: StateFlow<ThemeSetting>
    val dismissedClientVersionFlow: Flow<String?>
}
