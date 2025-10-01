package io.github.openflocon.flocondesktop.core.data.settings

import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.flocondesktop.core.data.settings.datasource.local.SettingsDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class SettingsRepositoryImpl(
    private val localSettingsDataSource: SettingsDataSource,
) : SettingsRepository {

    override val adbPath: Flow<String?> = localSettingsDataSource.adbPath
    override val fontSizeMultiplier: StateFlow<Float> = localSettingsDataSource.fontSizeMultiplier

    override fun getAdbPath(): String? = localSettingsDataSource.getAdbPath()

    override suspend fun setAdbPath(path: String) {
        localSettingsDataSource.setAdbPath(path)
    }

    override suspend fun setFontSizeMultiplier(value: Float) {
        localSettingsDataSource.setFontSizeMultiplier(value)
    }

}
