package io.github.openflocon.flocondesktop.features.grpc.domain.repository

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.grpc.domain.model.GrpcCallDomainModel
import io.github.openflocon.flocondesktop.features.grpc.domain.model.GrpcCallId
import kotlinx.coroutines.flow.Flow

interface GRPCRepository {
    fun observeCalls(deviceId: DeviceId): Flow<List<GrpcCallDomainModel>>
    fun observeCall(currentDeviceId: DeviceId, callId: GrpcCallId): Flow<GrpcCallDomainModel?>

    suspend fun deleteCall(deviceId: DeviceId, callId: GrpcCallId)
    suspend fun deleteCallsBefore(deviceId: DeviceId, callId: GrpcCallId)
    suspend fun deleteCallsForDevice(deviceId: DeviceId)
}
