package io.github.openflocon.flocondesktop.core.data.settings

import io.github.openflocon.domain.models.settings.NetworkSettings
import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.flocondesktop.core.data.settings.datasource.local.SettingsDataSource
import io.github.openflocon.flocondesktop.core.data.settings.models.toDomain
import io.github.openflocon.flocondesktop.core.data.settings.models.toLocal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest

internal class SettingsRepositoryImpl(
    private val localSettingsDataSource: SettingsDataSource,
) : SettingsRepository {

    override val adbPath: Flow<String?> = localSettingsDataSource.adbPath
    override val fontSizeMultiplier: StateFlow<Float> = localSettingsDataSource.fontSizeMultiplier

    override var networkSettings: NetworkSettings
        get() = localSettingsDataSource.networkSettings.toDomain()
        set(value) {
            localSettingsDataSource.networkSettings = value.toLocal()
        }
    override val networkSettingsFlow: Flow<NetworkSettings> = localSettingsDataSource.networkSettingsFlow
        .mapLatest { it.toDomain() }

    override fun getAdbPath(): String? = localSettingsDataSource.getAdbPath()

    override suspend fun setAdbPath(path: String) {
        localSettingsDataSource.setAdbPath(path)
    }

    override suspend fun setFontSizeMultiplier(value: Float) {
        localSettingsDataSource.setFontSizeMultiplier(value)
    }
}
