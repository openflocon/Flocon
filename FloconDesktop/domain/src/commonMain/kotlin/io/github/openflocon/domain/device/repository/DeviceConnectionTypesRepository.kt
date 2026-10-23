package io.github.openflocon.domain.device.repository

import io.github.openflocon.domain.device.models.DeviceConnectionTypeDomainModel
import io.github.openflocon.domain.device.models.DeviceId
import kotlinx.coroutines.flow.Flow

interface DeviceConnectionTypesRepository {
    // how each device connected the last time we saw it
    val lastConnectionTypes: Flow<Map<DeviceId, DeviceConnectionTypeDomainModel>>

    suspend fun setConnectionTypes(types: Map<DeviceId, DeviceConnectionTypeDomainModel>)
}
