package io.github.openflocon.domain.settings.repository

import io.github.openflocon.domain.models.settings.NetworkSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    var networkSettings: NetworkSettings
    val networkSettingsFlow: Flow<NetworkSettings>

    fun getAdbPath(): String?

    suspend fun setAdbPath(path: String)

    suspend fun setFontSizeMultiplier(value: Float)

    val adbPath: Flow<String?>
    val fontSizeMultiplier: StateFlow<Float>
}
