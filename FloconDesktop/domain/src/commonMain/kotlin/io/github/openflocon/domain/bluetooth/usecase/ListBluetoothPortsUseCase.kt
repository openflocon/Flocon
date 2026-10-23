package io.github.openflocon.domain.bluetooth.usecase

import io.github.openflocon.domain.bluetooth.repository.BluetoothBridgeRepository

class ListBluetoothPortsUseCase(
    private val bluetoothBridgeRepository: BluetoothBridgeRepository,
) {
    operator fun invoke(): List<String> = bluetoothBridgeRepository.listPorts()
}
