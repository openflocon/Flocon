package io.github.openflocon.domain.database.usecase

import io.github.openflocon.domain.database.models.DatabaseQueryLogDomainModel
import io.github.openflocon.domain.database.repository.DatabaseRepository
import kotlinx.coroutines.flow.Flow

class ObserveDatabaseQueryLogUseCase(
    private val databaseRepository: DatabaseRepository,
) {
    operator fun invoke(id: Int): Flow<DatabaseQueryLogDomainModel?> = databaseRepository.observeQueryLog(id)
}
