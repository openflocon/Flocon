package io.github.openflocon.domain.database.usecase

import androidx.paging.PagingData
import io.github.openflocon.domain.database.models.DatabaseQueryLogDomainModel
import io.github.openflocon.domain.database.models.FilterQueryLogDomainModel
import io.github.openflocon.domain.database.repository.DatabaseRepository
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveDatabaseQueryLogsUseCase(
    private val databaseRepository: DatabaseRepository,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(
        dbName: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>
    ): Flow<PagingData<DatabaseQueryLogDomainModel>> {
        return observeCurrentDeviceIdAndPackageNameUseCase().flatMapLatest { current ->
            if (current == null) {
                return@flatMapLatest flowOf(PagingData.empty())
            } else {
                databaseRepository.observeQueryLogs(
                    dbName = dbName,
                    showTransactions = showTransactions,
                    filters = filters,
                    deviceIdAndPackageName = current,
                )
            }
        }
    }
}
