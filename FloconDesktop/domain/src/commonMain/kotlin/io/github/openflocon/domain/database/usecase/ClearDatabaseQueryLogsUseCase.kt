package io.github.openflocon.domain.database.usecase

import io.github.openflocon.domain.database.repository.DatabaseRepository
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import kotlinx.coroutines.flow.firstOrNull

class ClearDatabaseQueryLogsUseCase(
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val databaseRepository: DatabaseRepository,
) {
    suspend operator fun invoke() {
        observeCurrentDeviceIdAndPackageNameUseCase().firstOrNull()?.let {
            databaseRepository.deleteAllQueryLogs(it)
        }
    }
}
