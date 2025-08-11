package io.github.openflocon.domain.database.usecase

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.database.repository.DatabaseRepository

class AskForDeviceDatabasesUseCase(
    private val databaseRepository: DatabaseRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke() {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return

        return databaseRepository.askForDeviceDatabases(deviceIdAndPackageName = current)
    }
}
