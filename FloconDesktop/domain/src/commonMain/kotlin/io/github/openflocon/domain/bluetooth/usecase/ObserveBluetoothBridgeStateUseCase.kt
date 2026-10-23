package io.github.openflocon.domain.bluetooth.usecase

import io.github.openflocon.domain.bluetooth.models.BluetoothBridgeStateDomainModel
import io.github.openflocon.domain.bluetooth.repository.BluetoothBridgeRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveBluetoothBridgeStateUseCase(
    private val bluetoothBridgeRepository: BluetoothBridgeRepository,
) {
    operator fun invoke(): StateFlow<BluetoothBridgeStateDomainModel> = bluetoothBridgeRepository.state
}
