package io.github.openflocon.flocondesktop.features.grpc.data.datasource

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.features.grpc.domain.model.GrpcCallDomainModel
import com.florent37.flocondesktop.features.grpc.domain.model.GrpcCallId
import com.florent37.flocondesktop.features.grpc.domain.model.GrpcRequestDomainModel
import com.florent37.flocondesktop.features.grpc.domain.model.GrpcResponseDomainModel
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
