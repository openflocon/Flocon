package com.florent37.flocondesktop.core.data.settings.datasource.local

import kotlinx.coroutines.flow.Flow

interface SettingsDataSource {
    fun getAdbPath(): String?

    suspend fun setAdbPath(path: String)

    val adbPath: Flow<String?>
}
