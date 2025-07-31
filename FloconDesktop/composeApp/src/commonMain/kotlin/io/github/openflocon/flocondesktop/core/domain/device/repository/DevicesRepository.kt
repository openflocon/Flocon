package io.github.openflocon.flocondesktop.core.domain.device.repository

import io.github.openflocon.flocondesktop.messages.domain.model.DeviceDomainModel
import kotlinx.coroutines.flow.Flow

interface DevicesRepository {
    val devices: Flow<List<DeviceDomainModel>>

    suspend fun register(device: DeviceDomainModel)

    suspend fun unregister(device: DeviceDomainModel)

    suspend fun clear()

    val currentDevice: Flow<DeviceDomainModel?>

    fun getCurrentDevice(): DeviceDomainModel?

    suspend fun selectDevice(device: DeviceDomainModel)
}
