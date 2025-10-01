package io.github.openflocon.flocondesktop.core.data.settings.datasource.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SettingsDataSource {
    fun getAdbPath(): String?

    suspend fun setAdbPath(path: String)
    suspend fun setFontSizeMultiplier(value: Float)

    val adbPath: Flow<String?>
    val fontSizeMultiplier: StateFlow<Float>
}
