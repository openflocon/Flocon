package io.github.openflocon.domain.database.usecase.favorite

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.database.repository.DatabaseRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase

class DeleteFavoriteQueryDatabaseUseCase(
    private val databaseRepository: DatabaseRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke(
        id: Long,
        databaseId: String,
    ): Either<Throwable, Unit> {
        val current = getCurrentDeviceIdAndPackageNameUseCase()
            ?: return Failure(Throwable("no current device"))

        return databaseRepository.deleteFavorite(
            deviceIdAndPackageName = current,
            databaseId = databaseId,
            id = id,
        )
    }
}
