package io.github.openflocon.flocondesktop.features.database.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.flocondesktop.features.database.domain.repository.DatabaseRepository

class AskForDeviceDatabasesUseCase(
    private val databaseRepository: DatabaseRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke() {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return

        return databaseRepository.askForDeviceDatabases(deviceIdAndPackageName = current)
    }
}
