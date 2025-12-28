package io.github.openflocon.domain.database.usecase

import androidx.paging.PagingData
import androidx.paging.PagingSource
import io.github.openflocon.domain.database.models.DatabaseQueryLogDomainModel
import io.github.openflocon.domain.database.repository.DatabaseRepository
import kotlinx.coroutines.flow.Flow

class ObserveDatabaseQueryLogsUseCase(
    private val databaseRepository: DatabaseRepository,
) {
    operator fun invoke(dbName: String, showTransactions: Boolean, filterChips: List<String>): Flow<PagingData<DatabaseQueryLogDomainModel>> {
        return databaseRepository.observeQueryLogs(dbName, showTransactions, filterChips)
    }
}
