package io.github.openflocon.domain.settings.repository

import kotlinx.coroutines.flow.Flow

interface MascotPreferencesRepository {
    val isEnabled: Flow<Boolean>

    suspend fun setEnabled(enabled: Boolean)
}
