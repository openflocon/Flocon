package io.github.openflocon.domain.database.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.database.models.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.domain.database.repository.DatabaseRepository
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel

class ExecuteDatabaseQueryUseCase(
    private val databaseRepository: DatabaseRepository,
) {
    suspend operator fun invoke(
        query: String,
        databaseId: String,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Either<Throwable, DatabaseExecuteSqlResponseDomainModel> = databaseRepository.executeQuery(
        deviceIdAndPackageName = deviceIdAndPackageName,
        databaseId = databaseId,
        query = query,
    )
        .alsoSuccess {
            databaseRepository.saveSuccessQuery(
                deviceIdAndPackageName = deviceIdAndPackageName,
                databaseId = databaseId,
                query = query,
            )
        }
}
