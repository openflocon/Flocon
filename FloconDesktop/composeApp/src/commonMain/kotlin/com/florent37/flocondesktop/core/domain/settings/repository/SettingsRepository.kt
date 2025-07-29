package com.florent37.flocondesktop.core.domain.settings.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getAdbPath(): String?

    fun setAdbPath(path: String)

    val adbPath: Flow<String?>
}
