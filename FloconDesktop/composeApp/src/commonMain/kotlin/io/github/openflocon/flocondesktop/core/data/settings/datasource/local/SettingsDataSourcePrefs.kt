@file:OptIn(ExperimentalSettingsApi::class, ExperimentalSerializationApi::class)

package io.github.openflocon.flocondesktop.core.data.settings.datasource.local

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import io.github.openflocon.flocondesktop.core.data.settings.models.NetworkSettingsLocal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
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

    override val adbPath: Flow<String?> = flowSettings.getStringOrNullFlow(ADB_PATH)
    override val fontSizeMultiplier: StateFlow<Float> = settings.toFlowSettings()
        .getFloatOrNullFlow(FONT_SIZE_MULTIPLIER)
        .filterNotNull()
        .stateIn(1f)

    override fun getAdbPath(): String? = settings.getStringOrNull(ADB_PATH)

    override suspend fun setAdbPath(path: String) {
        settings.putString(ADB_PATH, path)
    }

    override suspend fun setFontSizeMultiplier(value: Float) {
        settings.putFloat(FONT_SIZE_MULTIPLIER, value)
    }

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
        private const val FONT_SIZE_MULTIPLIER = "font_size_multiplier"

        private const val NETWORK_SETTINGS = "network_settings"
    }
}
