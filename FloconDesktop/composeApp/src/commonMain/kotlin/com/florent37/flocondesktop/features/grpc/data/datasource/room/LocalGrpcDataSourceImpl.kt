package com.florent37.flocondesktop.features.grpc.data.datasource.room

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.features.grpc.data.datasource.LocalGrpcDataSource
import com.florent37.flocondesktop.features.grpc.data.datasource.room.model.GrpcCallEntity
import com.florent37.flocondesktop.features.grpc.data.datasource.room.model.GrpcResponseEntity
import com.florent37.flocondesktop.features.grpc.domain.model.GrpcCallDomainModel
import com.florent37.flocondesktop.features.grpc.domain.model.GrpcCallId
import com.florent37.flocondesktop.features.grpc.domain.model.GrpcRequestDomainModel
import com.florent37.flocondesktop.features.grpc.domain.model.GrpcResponseDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalGrpcDataSourceImpl(
    private val grpcDao: GrpcDao,
) : LocalGrpcDataSource {

    override suspend fun saveRequest(
        deviceId: String,
        callId: String,
        request: GrpcRequestDomainModel,
    ) {
        val headersMap = request.headers.associate { it.key to it.value }
        val requestEntity = GrpcCallEntity(
            callId = callId,
            deviceId = deviceId,
            request = GrpcCallEntity.Request(
                timestamp = request.timestamp,
                authority = request.authority,
                method = request.method,
                data = request.data,
                headers = headersMap,
            ),
        )
        grpcDao.insertGrpcCall(requestEntity)
    }

    override suspend fun saveResponse(
        deviceId: String,
        callId: String,
        response: GrpcResponseDomainModel,
    ) {
        val headersMap = response.headers.associate { it.key to it.value }
        val responseEntity = GrpcResponseEntity(
            callId = callId,
            responseTimestamp = response.timestamp,
            status = response.status,
            resultType = when (response.result) {
                is GrpcResponseDomainModel.CallResult.Success -> "success"
                is GrpcResponseDomainModel.CallResult.Error -> "error"
            },
            resultData = when (response.result) {
                is GrpcResponseDomainModel.CallResult.Success -> response.result.data
                is GrpcResponseDomainModel.CallResult.Error -> response.result.cause
            },
            headers = headersMap,
        )
        grpcDao.insertGrpcResponse(responseEntity)
    }

    override fun observeCalls(deviceId: String): Flow<List<GrpcCallDomainModel>> = grpcDao.observeCallsWithDetails(deviceId).map { entities ->
        entities.map { it.toDomainModel() }
    }

    override fun observeCall(deviceId: DeviceId, callId: GrpcCallId) = grpcDao.observeCallWithDetails(deviceId, callId = callId).map {
        it?.toDomainModel()
    }

    override suspend fun clearDeviceCalls(deviceId: String) {
        grpcDao.clearDeviceData(deviceId)
    }

    override suspend fun deleteCall(deviceId: String, callId: GrpcCallId) {
        grpcDao.deleteCallById(callId)
    }

    override suspend fun deleteCallsBefore(deviceId: String, callId: GrpcCallId) {
        val timestamp = grpcDao.getCallTimestamp(deviceId = deviceId, callId = callId) ?: return
        grpcDao.deleteCallsBeforeTimestamp(
            deviceId = deviceId,
            timestamp = timestamp,
        )
    }

    override suspend fun clear() {
        grpcDao.clearAllData()
    }
}
