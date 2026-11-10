package io.github.openflocon.domain.settings.repository

import kotlinx.coroutines.flow.Flow

interface TransactionPreferencesRepository {
    // the database transactions show their queries until the user folds them
    val expandedByDefault: Flow<Boolean>

    suspend fun setExpandedByDefault(expanded: Boolean)
}
