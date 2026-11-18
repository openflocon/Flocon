package io.github.openflocon.flocondesktop.core.data.settings

import io.github.openflocon.domain.settings.repository.TransactionPreferencesRepository
import io.github.openflocon.flocondesktop.core.data.settings.datasource.local.SettingsDataSource
import kotlinx.coroutines.flow.Flow

internal class TransactionPreferencesRepositoryImpl(
    private val localSettingsDataSource: SettingsDataSource,
) : TransactionPreferencesRepository {

    override val expandedByDefault: Flow<Boolean> = localSettingsDataSource.transactionsExpandedByDefault

    override suspend fun setExpandedByDefault(expanded: Boolean) {
        localSettingsDataSource.setTransactionsExpandedByDefault(expanded)
    }
}
