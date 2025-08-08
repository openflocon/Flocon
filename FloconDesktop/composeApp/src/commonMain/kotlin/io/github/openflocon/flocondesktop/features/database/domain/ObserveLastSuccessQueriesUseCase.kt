package io.github.openflocon.flocondesktop.features.database.domain

import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.flocondesktop.features.database.domain.repository.DatabaseRepository
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveLastSuccessQueriesUseCase(
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val databaseRepository: DatabaseRepository,
) {
    operator fun invoke() = observeCurrentDeviceIdAndPackageNameUseCase().flatMapLatest { model ->
        if (model == null) {
            flowOf(emptyList())
        } else {
            databaseRepository.observeSelectedDeviceDatabase(deviceIdAndPackageName = model).flatMapLatest { database ->
                if (database == null) {
                    flowOf(emptyList())
                } else {
                    databaseRepository.observeLastSuccessQuery(deviceIdAndPackageName = model, databaseId = database.id)
                }
            }
        }
    }
}
