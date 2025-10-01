@file:OptIn(ExperimentalSettingsApi::class)

package io.github.openflocon.flocondesktop.core.data.settings.datasource.local

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn

expect fun createSettings(): ObservableSettings

class SettingsDataSourcePrefs(
    applicationScope: CoroutineScope
) : SettingsDataSource {
    private val settings = createSettings()

    private val flowSettings = settings.toFlowSettings()

    override val adbPath: Flow<String?> = flowSettings.getStringOrNullFlow(ADB_PATH)
    override val fontSizeMultiplier: StateFlow<Float> = settings.toFlowSettings()
        .getFloatOrNullFlow(FONT_SIZE_MULTIPLIER)
        .filterNotNull()
        .stateIn(
            scope = applicationScope,
            started = SharingStarted.Lazily,
            initialValue = 1f
        )

    override fun getAdbPath(): String? = settings.getStringOrNull(ADB_PATH)

    override suspend fun setAdbPath(path: String) {
        settings.putString(ADB_PATH, path)
    }

    override suspend fun setFontSizeMultiplier(value: Float) {
        settings.putFloat(FONT_SIZE_MULTIPLIER, value)
    }

    companion object {
        private const val ADB_PATH = "adb_path"
        private const val FONT_SIZE_MULTIPLIER = "font_size_multiplier"
    }
}
