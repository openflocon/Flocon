package io.github.openflocon.domain.database.usecase

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.database.models.DeviceDataBaseId
import io.github.openflocon.domain.database.repository.DatabaseRepository

class SelectCurrentDeviceDatabaseUseCase(
    private val databaseRepository: DatabaseRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(databaseId: DeviceDataBaseId) {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return

        databaseRepository.selectDeviceDatabase(
            deviceIdAndPackageName = current,
            databaseId = databaseId,
        )
    }
}
