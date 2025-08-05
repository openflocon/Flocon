@file:OptIn(ExperimentalSettingsApi::class)

package io.github.openflocon.flocondesktop.core.data.settings.datasource.local

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import kotlinx.coroutines.flow.Flow

// Expect class pour obtenir les Settings de manière multiplateforme
expect fun createSettings(): ObservableSettings

class SettingsDataSourcePrefs : SettingsDataSource {
    private val settings = createSettings()

    private val flowSettings = settings.toFlowSettings()

    override fun getAdbPath(): String? = settings.getStringOrNull(ADB_PATH)

    override suspend fun setAdbPath(path: String) {
        settings.putString(ADB_PATH, path)
    }

    override val adbPath: Flow<String?> = flowSettings.getStringOrNullFlow(ADB_PATH)

    companion object {
        private const val ADB_PATH = "adb_path"
    }
}
