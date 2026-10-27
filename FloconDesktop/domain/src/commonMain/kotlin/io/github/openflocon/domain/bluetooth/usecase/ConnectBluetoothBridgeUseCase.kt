package io.github.openflocon.domain.bluetooth.usecase

import io.github.openflocon.domain.bluetooth.repository.BluetoothBridgeRepository

class ConnectBluetoothBridgeUseCase(
    private val bluetoothBridgeRepository: BluetoothBridgeRepository,
) {
    operator fun invoke(port: String) = bluetoothBridgeRepository.connect(port)
}
