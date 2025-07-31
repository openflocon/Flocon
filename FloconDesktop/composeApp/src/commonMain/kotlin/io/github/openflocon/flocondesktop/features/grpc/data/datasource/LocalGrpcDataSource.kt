package io.github.openflocon.flocondesktop.features.grpc.data.datasource

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.grpc.domain.model.GrpcCallDomainModel
import io.github.openflocon.flocondesktop.features.grpc.domain.model.GrpcCallId
import io.github.openflocon.flocondesktop.features.grpc.domain.model.GrpcRequestDomainModel
import io.github.openflocon.flocondesktop.features.grpc.domain.model.GrpcResponseDomainModel
import kotlinx.coroutines.flow.Flow

interface LocalGrpcDataSource {
    suspend fun saveRequest(deviceId: DeviceId, callId: GrpcCallId, request: GrpcRequestDomainModel)
    suspend fun saveResponse(deviceId: DeviceId, callId: GrpcCallId, response: GrpcResponseDomainModel)
    fun observeCalls(deviceId: DeviceId): Flow<List<GrpcCallDomainModel>>
    fun observeCall(deviceId: DeviceId, callId: GrpcCallId): Flow<GrpcCallDomainModel?>
    suspend fun clearDeviceCalls(deviceId: DeviceId)

    suspend fun deleteCall(deviceId: DeviceId, callId: GrpcCallId)
    suspend fun deleteCallsBefore(deviceId: DeviceId, callId: GrpcCallId)

    suspend fun clear()
}
