package io.github.openflocon.domain.database.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.database.models.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.domain.database.repository.DatabaseRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase

class ExecuteDatabaseQueryUseCase(
    private val databaseRepository: DatabaseRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke(
        query: String,
        databaseId: String,
    ): Either<Throwable, DatabaseExecuteSqlResponseDomainModel> {
        val current = getCurrentDeviceIdAndPackageNameUseCase()
            ?: return Failure(Throwable("no current device"))

        return databaseRepository.executeQuery(
            deviceIdAndPackageName = current,
            databaseId = databaseId,
            query = query,
        )
            .alsoSuccess {
                databaseRepository.saveSuccessQuery(
                    deviceIdAndPackageName = current,
                    databaseId = databaseId,
                    query = query,
                )
            }
    }
}
