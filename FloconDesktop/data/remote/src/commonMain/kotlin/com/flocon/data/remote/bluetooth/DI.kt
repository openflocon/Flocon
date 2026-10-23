package com.flocon.data.remote.bluetooth

import io.github.openflocon.domain.bluetooth.repository.BluetoothBridgeRepository
import org.koin.dsl.module

internal val bluetoothModule = module {
    single<BluetoothBridgeRepository> { getBluetoothBridgeRepository() }
}

internal expect fun getBluetoothBridgeRepository(): BluetoothBridgeRepository
