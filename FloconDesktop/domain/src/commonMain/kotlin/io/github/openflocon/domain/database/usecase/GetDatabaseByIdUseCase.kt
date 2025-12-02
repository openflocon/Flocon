package io.github.openflocon.domain.database.usecase

import io.github.openflocon.domain.database.models.DeviceDataBaseDomainModel
import io.github.openflocon.domain.database.repository.DatabaseRepository

class GetDatabaseByIdUseCase(
    private val databaseRepository: DatabaseRepository,
) {
    suspend operator fun invoke(databaseId: String): DeviceDataBaseDomainModel? = databaseRepository.getDatabaseById(databaseId)
}
