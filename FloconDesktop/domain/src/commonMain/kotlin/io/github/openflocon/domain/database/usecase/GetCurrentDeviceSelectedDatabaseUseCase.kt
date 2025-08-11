package io.github.openflocon.domain.database.usecase

import io.github.openflocon.domain.database.models.DeviceDataBaseDomainModel
import kotlinx.coroutines.flow.firstOrNull

class GetCurrentDeviceSelectedDatabaseUseCase(
    private val observeCurrentDeviceSelectedDatabaseUseCase: ObserveCurrentDeviceSelectedDatabaseUseCase,
) {
    suspend operator fun invoke(): DeviceDataBaseDomainModel? = observeCurrentDeviceSelectedDatabaseUseCase().firstOrNull()
}
