package io.github.openflocon.flocondesktop.features.database.domain

import com.florent37.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.database.domain.repository.DatabaseRepository

class AskForDeviceDatabasesUseCase(
    private val databaseRepository: DatabaseRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
) {
    suspend operator fun invoke() {
        val currentDeviceId = getCurrentDeviceIdUseCase() ?: return
        return databaseRepository.askForDeviceDatabases(
            deviceId = currentDeviceId,
        )
    }
}
