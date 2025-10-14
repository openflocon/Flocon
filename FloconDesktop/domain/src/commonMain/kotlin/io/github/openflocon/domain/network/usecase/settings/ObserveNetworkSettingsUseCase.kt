package io.github.openflocon.domain.network.usecase.settings

import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdUseCase
import io.github.openflocon.domain.models.TextFilterStateDomainModel
import io.github.openflocon.domain.network.models.NetworkSettingsDomainModel
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import io.github.openflocon.domain.network.repository.NetworkFilterRepository
import io.github.openflocon.domain.network.repository.NetworkSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveNetworkSettingsUseCase(
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val networkSettingsRepository: NetworkSettingsRepository,
) {
    operator fun invoke(): Flow<NetworkSettingsDomainModel?> =
        observeCurrentDeviceIdAndPackageNameUseCase().flatMapLatest { current ->
            if (current == null) flowOf(null)
            else networkSettingsRepository.observeNetworkSettings(deviceAndApp = current)
        }.distinctUntilChanged()
}
