package io.github.openflocon.domain.database.usecase.favorite

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.database.repository.DatabaseRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase

class SaveQueryAsFavoriteDatabaseUseCase(
    private val databaseRepository: DatabaseRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke(
        query: String,
        title: String,
        databaseId: String,
    ): Either<Throwable, Unit> {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return Failure(Throwable("no current device"))

        return databaseRepository.saveAsFavorite(
            deviceIdAndPackageName = current,
            databaseId = databaseId,
            title = title,
            query = query,
        )
    }
}
