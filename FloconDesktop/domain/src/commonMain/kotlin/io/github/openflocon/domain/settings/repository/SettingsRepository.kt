package io.github.openflocon.domain.settings.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getAdbPath(): String?

    suspend fun setAdbPath(path: String)

    val adbPath: Flow<String?>
}
