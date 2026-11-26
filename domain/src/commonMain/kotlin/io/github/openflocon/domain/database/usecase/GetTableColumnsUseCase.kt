package io.github.openflocon.domain.database.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.database.models.DatabaseTableDomainModel
import io.github.openflocon.domain.database.repository.DatabaseRepository
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel

class GetTableColumnsUseCase(
    private val databaseRepository: DatabaseRepository,
) {
    suspend operator fun invoke(
        tableName: String,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: String
    ): Either<Throwable, Unit> = databaseRepository.executeQuery(
        deviceIdAndPackageName = deviceIdAndPackageName,
        databaseId = databaseId,
        query = "PRAGMA table_info($tableName)",
    ).mapSuccess {
        val columns = extractTableColumns(it)
        DatabaseTableDomainModel(
            tableName = tableName,
            columns = columns,
        )
    }.alsoSuccess {
        databaseRepository.saveTable(
            deviceIdAndPackageName = deviceIdAndPackageName,
            databaseId = databaseId,
            table = it,
        )
    }.mapSuccess { }
}
