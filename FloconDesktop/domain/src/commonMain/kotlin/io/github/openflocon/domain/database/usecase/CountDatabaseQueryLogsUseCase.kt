package io.github.openflocon.domain.database.usecase

import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.database.models.FilterQueryLogDomainModel
import io.github.openflocon.domain.database.repository.DatabaseRepository
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

class CountDatabaseQueryLogsUseCase(
    private val databaseRepository: DatabaseRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(
        dbName: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>
    ): Flow<Int> {
        return observeCurrentDeviceIdAndPackageNameUseCase().flatMapLatest { current ->
            if (current == null) {
                return@flatMapLatest flowOf(0)
            } else {
                databaseRepository.countQueryLogs(
                    dbName = dbName,
                    showTransactions = showTransactions,
                    filters = filters,
                    deviceIdAndPackageName = current,
                )
            }
        }.flowOn(dispatcherProvider.domain)
    }
}
