package com.florent37.flocondesktop.features.database.domain

import com.florent37.flocondesktop.common.Either
import com.florent37.flocondesktop.common.Failure
import com.florent37.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.database.domain.model.DatabaseExecuteSqlResponseDomainModel
import com.florent37.flocondesktop.features.database.domain.repository.DatabaseRepository

class ExecuteDatabaseQueryUseCase(
    private val databaseRepository: DatabaseRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
    private val getCurrentDeviceSelectedDatabaseUseCase: GetCurrentDeviceSelectedDatabaseUseCase,
) {
    suspend operator fun invoke(query: String): Either<Throwable, DatabaseExecuteSqlResponseDomainModel> {
        val currentDeviceId = getCurrentDeviceIdUseCase() ?: return Failure(Throwable("no current device"))
        val database = getCurrentDeviceSelectedDatabaseUseCase() ?: return Failure(Throwable("no selected database"))
        return databaseRepository.executeQuery(
            deviceId = currentDeviceId,
            databaseId = database.id,
            query = query,
        ).alsoSuccess {
            databaseRepository.saveSuccessQuery(
                deviceId = currentDeviceId,
                databaseId = database.id,
                query = query,
            )
        }
    }
}
