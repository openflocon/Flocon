package io.github.openflocon.flocondesktop.features.grpc.data

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.FloconIncomingMessageDataModel
import io.github.openflocon.flocondesktop.Protocol
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.features.grpc.data.datasource.LocalGrpcDataSource
import io.github.openflocon.flocondesktop.features.grpc.data.model.fromdevice.GrpcRequestDataModel
import io.github.openflocon.flocondesktop.features.grpc.data.model.fromdevice.GrpcResponseDataModel
import io.github.openflocon.flocondesktop.features.grpc.domain.model.GrpcCallDomainModel
import io.github.openflocon.flocondesktop.features.grpc.domain.model.GrpcCallId
import io.github.openflocon.flocondesktop.features.grpc.domain.repository.GRPCRepository
import io.github.openflocon.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class GRPCRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val localGrpcDataSource: LocalGrpcDataSource,
) : GRPCRepository,
    MessagesReceiverRepository {

    private val grpcParser =
        Json {
            ignoreUnknownKeys = true
        }

    override val pluginName = listOf(Protocol.FromDevice.GRPC.Plugin)

    override suspend fun onMessageReceived(
        deviceId: String,
        message: FloconIncomingMessageDataModel,
    ) {
        withContext(dispatcherProvider.data) {
            when (message.method) {
                Protocol.FromDevice.GRPC.Method.LogNetworkRequest -> decodeGrpcRequest(message)?.let {
                    toDomain(it)
                }?.let {
                    localGrpcDataSource.saveRequest(
                        deviceId = deviceId,
                        callId = it.callId,
                        request = it.request,
                    )
                }

                Protocol.FromDevice.GRPC.Method.LogNetworkResponse -> decodeGrpcResponse(message)?.let {
                    toDomain(
                        it,
                    )
                }?.let {
                    localGrpcDataSource.saveResponse(
                        deviceId = deviceId,
                        callId = it.callId,
                        response = it.response,
                    )
                }
            }
        }
    }

    private fun decodeGrpcRequest(message: FloconIncomingMessageDataModel): GrpcRequestDataModel? = try {
        grpcParser.decodeFromString<GrpcRequestDataModel>(message.body)
    } catch (t: Throwable) {
        t.printStackTrace()
        null
    }

    private fun decodeGrpcResponse(message: FloconIncomingMessageDataModel): GrpcResponseDataModel? = try {
        grpcParser.decodeFromString<GrpcResponseDataModel>(message.body)
    } catch (t: Throwable) {
        t.printStackTrace()
        null
    }

    override fun observeCalls(deviceId: DeviceId): Flow<List<GrpcCallDomainModel>> = localGrpcDataSource.observeCalls(
        deviceId = deviceId,
    ).flowOn(dispatcherProvider.data)

    override fun observeCall(
        currentDeviceId: DeviceId,
        callId: GrpcCallId,
    ): Flow<GrpcCallDomainModel?> = localGrpcDataSource.observeCall(
        deviceId = currentDeviceId,
        callId = callId,
    ).flowOn(dispatcherProvider.data)

    override suspend fun deleteCall(deviceId: DeviceId, callId: GrpcCallId) = withContext(dispatcherProvider.data) {
        localGrpcDataSource.deleteCall(deviceId = deviceId, callId = callId)
    }
    override suspend fun deleteCallsBefore(deviceId: DeviceId, callId: GrpcCallId) = withContext(dispatcherProvider.data) {
        localGrpcDataSource.deleteCallsBefore(deviceId = deviceId, callId = callId)
    }

    override suspend fun deleteCallsForDevice(deviceId: DeviceId) = withContext(dispatcherProvider.data) {
        localGrpcDataSource.clearDeviceCalls(deviceId = deviceId)
    }
}
