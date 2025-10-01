package io.github.openflocon.domain.settings.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    fun getAdbPath(): String?

    suspend fun setAdbPath(path: String)

    suspend fun setFontSizeMultiplier(value: Float)

    val adbPath: Flow<String?>
    val fontSizeMultiplier: StateFlow<Float>
}
