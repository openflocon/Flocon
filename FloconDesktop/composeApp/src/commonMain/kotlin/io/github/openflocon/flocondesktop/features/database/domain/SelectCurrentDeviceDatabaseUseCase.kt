package io.github.openflocon.flocondesktop.features.database.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdAndPackageNameUseCase
import com.flocon.library.domain.models.DeviceDataBaseId
import io.github.openflocon.flocondesktop.features.database.domain.repository.DatabaseRepository

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
