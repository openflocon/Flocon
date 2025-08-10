package io.github.openflocon.flocondesktop.features.database.domain

import io.github.openflocon.flocondesktop.common.Either
import io.github.openflocon.flocondesktop.common.Failure
import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.flocondesktop.features.database.domain.model.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.flocondesktop.features.database.domain.repository.DatabaseRepository

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
