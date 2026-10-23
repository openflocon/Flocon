package io.github.openflocon.domain.bluetooth

import io.github.openflocon.domain.bluetooth.usecase.ConnectBluetoothBridgeUseCase
import io.github.openflocon.domain.bluetooth.usecase.DisconnectBluetoothBridgeUseCase
import io.github.openflocon.domain.bluetooth.usecase.ListBluetoothPortsUseCase
import io.github.openflocon.domain.bluetooth.usecase.ObserveBluetoothBridgeStateUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val bluetoothModule = module {
    factoryOf(::ConnectBluetoothBridgeUseCase)
    factoryOf(::DisconnectBluetoothBridgeUseCase)
    factoryOf(::ListBluetoothPortsUseCase)
    factoryOf(::ObserveBluetoothBridgeStateUseCase)
}
