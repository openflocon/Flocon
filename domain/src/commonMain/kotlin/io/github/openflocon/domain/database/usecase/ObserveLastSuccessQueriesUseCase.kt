package io.github.openflocon.domain.database.usecase

import io.github.openflocon.domain.database.repository.DatabaseRepository
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveLastSuccessQueriesUseCase(
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val databaseRepository: DatabaseRepository,
) {
    operator fun invoke(
        databaseId: String,
    ): Flow<List<String>> = observeCurrentDeviceIdAndPackageNameUseCase().flatMapLatest { model ->
        if (model == null) {
            flowOf(emptyList())
        } else {
            databaseRepository.observeLastSuccessQuery(
                deviceIdAndPackageName = model,
                databaseId = databaseId,
            )
        }
    }
}
