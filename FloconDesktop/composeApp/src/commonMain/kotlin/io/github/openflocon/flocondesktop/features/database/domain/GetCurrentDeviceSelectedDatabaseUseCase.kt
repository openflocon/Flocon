package io.github.openflocon.flocondesktop.features.database.domain

import io.github.openflocon.flocondesktop.features.database.domain.model.DeviceDataBaseDomainModel
import kotlinx.coroutines.flow.firstOrNull

class GetCurrentDeviceSelectedDatabaseUseCase(
    private val observeCurrentDeviceSelectedDatabaseUseCase: ObserveCurrentDeviceSelectedDatabaseUseCase,
) {
    suspend operator fun invoke(): DeviceDataBaseDomainModel? = observeCurrentDeviceSelectedDatabaseUseCase().firstOrNull()
}
