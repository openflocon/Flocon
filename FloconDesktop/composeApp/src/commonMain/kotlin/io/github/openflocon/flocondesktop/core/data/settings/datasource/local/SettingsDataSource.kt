package io.github.openflocon.flocondesktop.core.data.settings.datasource.local

import io.github.openflocon.flocondesktop.core.data.settings.models.NetworkSettingsLocal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

internal interface SettingsDataSource {
    var networkSettings: NetworkSettingsLocal
    val networkSettingsFlow: StateFlow<NetworkSettingsLocal>

    fun getAdbPath(): String?

    suspend fun setAdbPath(path: String)
    suspend fun setFontSizeMultiplier(value: Float)

    val adbPath: Flow<String?>
    val fontSizeMultiplier: StateFlow<Float>
}
