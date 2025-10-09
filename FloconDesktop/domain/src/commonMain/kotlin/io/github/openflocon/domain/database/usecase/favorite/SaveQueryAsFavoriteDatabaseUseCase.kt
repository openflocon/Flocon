package io.github.openflocon.domain.database.usecase.favorite

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.database.repository.DatabaseRepository
import io.github.openflocon.domain.database.usecase.GetCurrentDeviceSelectedDatabaseUseCase
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase

class SaveQueryAsFavoriteDatabaseUseCase(
    private val databaseRepository: DatabaseRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val getCurrentDeviceSelectedDatabaseUseCase: GetCurrentDeviceSelectedDatabaseUseCase,
) {
    suspend operator fun invoke(
        query: String,
        title: String,
        databaseId: String?,
    ) : Either<Throwable, Unit> {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return Failure(Throwable("no current device"))
        val databaseId =
            databaseId ?: getCurrentDeviceSelectedDatabaseUseCase()?.id ?: return Failure(
                Throwable("no selected database")
            )

        return databaseRepository.saveAsFavorite(
            deviceIdAndPackageName = current,
            databaseId = databaseId,
            title = title,
            query = query,
        )
    }

}