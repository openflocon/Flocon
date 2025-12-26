package io.github.openflocon.domain.database.usecase

import androidx.paging.PagingSource
import io.github.openflocon.domain.database.models.DatabaseQueryLogDomainModel
import io.github.openflocon.domain.database.repository.DatabaseRepository

class GetDatabaseQueryLogsUseCase(
    private val databaseRepository: DatabaseRepository,
) {
    operator fun invoke(dbName: String): PagingSource<Int, DatabaseQueryLogDomainModel> {
        return databaseRepository.getQueryLogsPagingSource(dbName)
    }
}
