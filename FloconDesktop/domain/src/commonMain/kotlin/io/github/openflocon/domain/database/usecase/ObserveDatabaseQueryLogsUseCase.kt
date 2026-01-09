package io.github.openflocon.domain.database.usecase

import androidx.paging.PagingData
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.database.models.DatabaseQueryLogDomainModel
import io.github.openflocon.domain.database.models.FilterQueryLogDomainModel
import io.github.openflocon.domain.database.repository.DatabaseRepository
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

class ObserveDatabaseQueryLogsUseCase(
    private val databaseRepository: DatabaseRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(
        dbName: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>,
        limit: Int,
        offset: Int,
    ): Flow<List<DatabaseQueryLogDomainModel>> {
        return observeCurrentDeviceIdAndPackageNameUseCase().flatMapLatest { current ->
            if (current == null) {
                return@flatMapLatest flowOf(emptyList())
            } else {
                databaseRepository.observeQueryLogs(
                    dbName = dbName,
                    showTransactions = showTransactions,
                    filters = filters,
                    deviceIdAndPackageName = current,
                    limit = limit,
                    offset = offset,
                )
            }
        }.flowOn(dispatcherProvider.domain)
    }
}
