package io.github.openflocon.domain.database.usecase

import io.github.openflocon.domain.database.models.DatabaseQueryLogDomainModel
import io.github.openflocon.domain.database.models.FilterQueryLogDomainModel
import io.github.openflocon.domain.database.repository.DatabaseRepository
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import kotlinx.coroutines.flow.first

class GetDatabaseQueryLogsUseCase(
    private val databaseRepository: DatabaseRepository,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke(
        dbName: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>
    ): List<DatabaseQueryLogDomainModel> {
        val current = observeCurrentDeviceIdAndPackageNameUseCase().first() ?: return emptyList()
        return databaseRepository.getQueryLogs(
            dbName = dbName,
            showTransactions = showTransactions,
            filters = filters,
            deviceIdAndPackageName = current,
        )
    }
}
