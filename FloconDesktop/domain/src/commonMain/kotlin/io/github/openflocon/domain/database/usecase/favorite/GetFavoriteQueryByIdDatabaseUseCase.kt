package io.github.openflocon.domain.database.usecase.favorite

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.database.models.DatabaseFavoriteQueryDomainModel
import io.github.openflocon.domain.database.repository.DatabaseRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase

class GetFavoriteQueryByIdDatabaseUseCase(
    private val databaseRepository: DatabaseRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke(
        id: Long,
        databaseId: String,
    ): DatabaseFavoriteQueryDomainModel? {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return null

        return databaseRepository.getFavorite(
            deviceIdAndPackageName = current,
            databaseId = databaseId,
            id = id,
        )
    }

}