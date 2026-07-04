package io.github.openflocon.domain.settings.repository

import io.github.openflocon.domain.models.settings.NetworkSettings
import io.github.openflocon.domain.models.settings.ThemeSetting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    var networkSettings: NetworkSettings
    val networkSettingsFlow: Flow<NetworkSettings>

    fun getAdbPath(): String?

    suspend fun setAdbPath(path: String)

    suspend fun setFontSizeMultiplier(value: Float)

    suspend fun setTheme(value: ThemeSetting)

    val adbPath: Flow<String?>
    val fontSizeMultiplier: StateFlow<Float>
    val theme: StateFlow<ThemeSetting>
}
