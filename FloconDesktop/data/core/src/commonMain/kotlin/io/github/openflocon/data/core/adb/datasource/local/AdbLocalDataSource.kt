package io.github.openflocon.data.core.adb.datasource.local

import io.github.openflocon.domain.adb.model.DeviceWithSerialDomainModel
import kotlinx.coroutines.flow.Flow

interface AdbLocalDataSource {
    val devicesWithSerial: Flow<Set<DeviceWithSerialDomainModel>>
    suspend fun add(item: DeviceWithSerialDomainModel)
    suspend fun getFromDeviceId(deviceId: String) : DeviceWithSerialDomainModel?
}
