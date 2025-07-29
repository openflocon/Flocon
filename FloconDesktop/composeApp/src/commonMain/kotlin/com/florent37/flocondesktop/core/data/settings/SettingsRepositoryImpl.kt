package com.florent37.flocondesktop.core.data.settings

import com.florent37.flocondesktop.core.data.settings.datasource.local.SettingsDataSource
import com.florent37.flocondesktop.core.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val localSettingsDataSource: SettingsDataSource,
) : SettingsRepository {
    override fun getAdbPath(): String? = localSettingsDataSource.getAdbPath()

    override fun setAdbPath(path: String) {
        localSettingsDataSource.setAdbPath(path)
    }

    override val adbPath: Flow<String?> = localSettingsDataSource.adbPath
}
