package io.github.openflocon.domain.database.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.database.models.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.domain.database.repository.DatabaseRepository

class ExecuteDatabaseQueryUseCase(
    private val databaseRepository: DatabaseRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val getCurrentDeviceSelectedDatabaseUseCase: GetCurrentDeviceSelectedDatabaseUseCase,
) {
    suspend operator fun invoke(query: String): Either<Throwable, DatabaseExecuteSqlResponseDomainModel> {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return Failure(Throwable("no current device"))
        val database = getCurrentDeviceSelectedDatabaseUseCase() ?: return Failure(Throwable("no selected database"))

        return databaseRepository.executeQuery(
            deviceIdAndPackageName = current,
            databaseId = database.id,
            query = query,
        )
            .alsoSuccess {
                databaseRepository.saveSuccessQuery(
                    deviceIdAndPackageName = current,
                    databaseId = database.id,
                    query = query,
                )
            }
    }
}
