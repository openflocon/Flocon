package io.github.openflocon.flocondesktop.core.data.settings

import io.github.openflocon.flocondesktop.core.data.settings.datasource.local.SettingsDataSource
import io.github.openflocon.flocondesktop.core.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val localSettingsDataSource: SettingsDataSource,
) : SettingsRepository {
    override fun getAdbPath(): String? = localSettingsDataSource.getAdbPath()

    override suspend fun setAdbPath(path: String) {
        localSettingsDataSource.setAdbPath(path)
    }

    override val adbPath: Flow<String?> = localSettingsDataSource.adbPath
}
