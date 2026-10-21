@file:OptIn(ExperimentalSettingsApi::class, ExperimentalSerializationApi::class)

package io.github.openflocon.flocondesktop.core.data.settings.datasource.local

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import io.github.openflocon.domain.models.settings.AppScaleSetting
import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.flocondesktop.core.data.settings.models.NetworkSettingsLocal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

expect fun createSettings(): ObservableSettings

internal class SettingsDataSourcePrefs(
    private val applicationScope: CoroutineScope
) : SettingsDataSource {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val settings = createSettings()

    private val flowSettings = settings.toFlowSettings()

    override var networkSettings: NetworkSettingsLocal by NETWORK_SETTINGS.classDelegateOf(NetworkSettingsLocal())
    override val networkSettingsFlow = flowSettings.getStringOrNullFlow(NETWORK_SETTINGS)
        .filterNotNull()
        .mapLatest { json.decodeFromString<NetworkSettingsLocal>(it) }
        .stateIn(networkSettings)

    override val floconDocumentsPath: Flow<String?> = flowSettings.getStringOrNullFlow(FLOCON_DOCUMENTS_PATH)

    override val adbPath: Flow<String?> = flowSettings.getStringOrNullFlow(ADB_PATH)
    // falls back on the former float multiplier (FONT_SIZE_MULTIPLIER) until a preset is chosen
    override val appScale: StateFlow<AppScaleSetting> = combine(
        flowSettings.getStringOrNullFlow(APP_SCALE),
        flowSettings.getFloatOrNullFlow(FONT_SIZE_MULTIPLIER),
    ) { name, legacyMultiplier ->
        name?.let { AppScaleSetting.entries.firstOrNull { it.name == name } }
            ?: legacyMultiplier?.let { AppScaleSetting.fromLegacyMultiplier(it) }
            ?: AppScaleSetting.DEFAULT
    }.stateIn(AppScaleSetting.DEFAULT)

    override val theme: StateFlow<ThemeSetting> = flowSettings
        .getStringOrNullFlow(THEME)
        .mapLatest { it.toThemeSetting() }
        .stateIn(ThemeSetting.DEFAULT)

    override val pinnedMenuItems: Flow<List<String>> = flowSettings.getStringOrNullFlow(PINNED_MENU_ITEMS)
        .mapLatest { it.toIdList() }

    override val collapsedMenuSections: Flow<Set<String>> = flowSettings.getStringOrNullFlow(COLLAPSED_MENU_SECTIONS)
        .mapLatest { it.toIdList().toSet() }

    override suspend fun setPinnedMenuItems(items: List<String>) {
        settings.putString(PINNED_MENU_ITEMS, items.joinToString(ID_SEPARATOR))
    }

    override suspend fun setCollapsedMenuSections(sections: Set<String>) {
        settings.putString(COLLAPSED_MENU_SECTIONS, sections.joinToString(ID_SEPARATOR))
    }

    private fun String?.toIdList(): List<String> = this
        ?.split(ID_SEPARATOR)
        ?.filter { it.isNotEmpty() }
        .orEmpty()

    override fun getAdbPath(): String? = settings.getStringOrNull(ADB_PATH)

    override suspend fun setAdbPath(path: String) {
        settings.putString(ADB_PATH, path)
    }

    override fun getFloconDocumentsPath(): String? = settings.getStringOrNull(FLOCON_DOCUMENTS_PATH)

    override suspend fun setFloconDocumentsPath(path: String) {
        settings.putString(FLOCON_DOCUMENTS_PATH, path)
    }

    override suspend fun setAppScale(value: AppScaleSetting) {
        settings.putString(APP_SCALE, value.name)
    }

    override suspend fun setTheme(value: ThemeSetting) {
        settings.putString(THEME, value.name)
    }

    override fun getDismissedDesktopVersion(): String? = settings.getStringOrNull(DISMISSED_DESKTOP_VERSION)

    override suspend fun setDismissedDesktopVersion(version: String) {
        settings.putString(DISMISSED_DESKTOP_VERSION, version)
    }

    override val dismissedClientVersionFlow: Flow<String?> = flowSettings.getStringOrNullFlow(DISMISSED_CLIENT_VERSION)

    override fun getDismissedClientVersion(): String? = settings.getStringOrNull(DISMISSED_CLIENT_VERSION)

    override suspend fun setDismissedClientVersion(version: String) {
        settings.putString(DISMISSED_CLIENT_VERSION, version)
    }

    override fun getLastSeenReleaseNotesVersion(): String? = settings.getStringOrNull(LAST_SEEN_RELEASE_NOTES_VERSION)

    override suspend fun setLastSeenReleaseNotesVersion(version: String) {
        settings.putString(LAST_SEEN_RELEASE_NOTES_VERSION, version)
    }

    private fun String?.toThemeSetting(): ThemeSetting = this
        ?.let { name -> ThemeSetting.entries.firstOrNull { it.name == name } }
        ?: ThemeSetting.DEFAULT

    private fun <T> Flow<T>.stateIn(default: T) = stateIn(
        scope = applicationScope,
        started = SharingStarted.Lazily,
        initialValue = default
    )

    private inner class ClassDelegate<T>(
        private val key: String,
        private val default: T,
        private val serializer: KSerializer<T>
    ) : ReadWriteProperty<SettingsDataSourcePrefs, T?> {

        override fun getValue(thisRef: SettingsDataSourcePrefs, property: KProperty<*>): T = thisRef.settings.getStringOrNull(key)
            ?.let { json.decodeFromString(serializer, it) }
            ?: default

        override fun setValue(thisRef: SettingsDataSourcePrefs, property: KProperty<*>, value: T?) {
            thisRef.settings
                .putString(key = key, value = json.encodeToString(serializer, value ?: return))
        }
    }

    private inline fun <reified T : Any> String.classDelegateOf(default: T): ClassDelegate<T> = ClassDelegate(
        key = this,
        default = default,
        serializer = serializer<T>()
    )

    companion object {
        private const val ADB_PATH = "adb_path"
        private const val FLOCON_DOCUMENTS_PATH = "flocon_documents_path"
        private const val APP_SCALE = "app_scale"
        private const val FONT_SIZE_MULTIPLIER = "font_size_multiplier" // former setting, read for the migration
        private const val THEME = "theme"
        private const val DISMISSED_DESKTOP_VERSION = "dismissed_desktop_version"
        private const val DISMISSED_CLIENT_VERSION = "dismissed_client_version"
        private const val LAST_SEEN_RELEASE_NOTES_VERSION = "last_seen_release_notes_version"

        private const val NETWORK_SETTINGS = "network_settings"

        private const val PINNED_MENU_ITEMS = "pinned_menu_items"
        private const val COLLAPSED_MENU_SECTIONS = "collapsed_menu_sections"
        private const val ID_SEPARATOR = ","
    }
}
