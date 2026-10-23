package io.github.openflocon.domain.bluetooth.usecase

import io.github.openflocon.domain.bluetooth.repository.BluetoothBridgeRepository

class DisconnectBluetoothBridgeUseCase(
    private val bluetoothBridgeRepository: BluetoothBridgeRepository,
) {
    operator fun invoke() = bluetoothBridgeRepository.disconnect()
}
