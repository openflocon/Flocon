package io.github.openflocon.flocondesktop.core.data.settings

import io.github.openflocon.domain.settings.repository.MascotPreferencesRepository
import io.github.openflocon.flocondesktop.core.data.settings.datasource.local.SettingsDataSource
import kotlinx.coroutines.flow.Flow

internal class MascotPreferencesRepositoryImpl(
    private val localSettingsDataSource: SettingsDataSource,
) : MascotPreferencesRepository {

    override val isEnabled: Flow<Boolean> = localSettingsDataSource.mascotEnabled

    override suspend fun setEnabled(enabled: Boolean) {
        localSettingsDataSource.setMascotEnabled(enabled)
    }
}
