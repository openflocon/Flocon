package io.github.openflocon.flocondesktop.features.graphql.data

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.FloconIncomingMessageDataModel
import com.florent37.flocondesktop.Protocol
import com.florent37.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import com.florent37.flocondesktop.features.graphql.data.datasource.LocalGraphQlDataSource
import com.florent37.flocondesktop.features.graphql.data.model.FloconGraphQlRequestDataModel
import com.florent37.flocondesktop.features.graphql.domain.model.FloconGraphQlRequestInfos
import com.florent37.flocondesktop.features.graphql.domain.model.GraphQlRequestDomainModel
import com.florent37.flocondesktop.features.graphql.domain.model.GraphQlRequestId
import com.florent37.flocondesktop.features.graphql.domain.repository.GraphQlRepository
import com.florent37.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class GraphQlRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val localGraphQlDataSource: LocalGraphQlDataSource,
) : GraphQlRepository,
    MessagesReceiverRepository {

    private val graphQlParser =
        Json {
            ignoreUnknownKeys = true
        }

    override val pluginName = listOf(Protocol.FromDevice.Graphql.Plugin)

    override suspend fun onMessageReceived(
        deviceId: String,
        message: FloconIncomingMessageDataModel,
    ) {
        withContext(dispatcherProvider.data) {
            when (message.method) {
                Protocol.FromDevice.Graphql.Method.LogNetworkCall -> decode(message)?.let {
                    toDomain(it)
                }?.let {
                    localGraphQlDataSource.insert(
                        deviceId = deviceId,
                        request = it,
                    )
                }
            }
        }
    }

    private fun decode(message: FloconIncomingMessageDataModel): FloconGraphQlRequestDataModel? = try {
        graphQlParser.decodeFromString<FloconGraphQlRequestDataModel>(message.body)
    } catch (t: Throwable) {
        t.printStackTrace()
        null
    }

    override fun observeRequests(deviceId: DeviceId): Flow<List<GraphQlRequestDomainModel>> = localGraphQlDataSource.observeRequests(
        deviceId = deviceId,
    ).flowOn(dispatcherProvider.data)

    override fun observeRequest(
        currentDeviceId: DeviceId,
        requestId: GraphQlRequestId,
    ): Flow<GraphQlRequestDomainModel?> = localGraphQlDataSource.observeRequest(
        deviceId = currentDeviceId,
        requestId = requestId,
    ).flowOn(dispatcherProvider.data)

    override suspend fun deleteRequest(deviceId: DeviceId, requestId: GraphQlRequestId) = withContext(dispatcherProvider.data) {
        localGraphQlDataSource.deleteRequest(deviceId = deviceId, requestId = requestId)
    }

    override suspend fun deleteRequestsBefore(deviceId: DeviceId, requestId: GraphQlRequestId) = withContext(dispatcherProvider.data) {
        localGraphQlDataSource.deleteRequestsBefore(deviceId = deviceId, requestId = requestId)
    }

    override suspend fun deleteRequestsForDevice(deviceId: DeviceId) = withContext(dispatcherProvider.data) {
        localGraphQlDataSource.clearDeviceCalls(deviceId = deviceId)
    }

    @OptIn(ExperimentalUuidApi::class)
    fun toDomain(decoded: FloconGraphQlRequestDataModel): GraphQlRequestDomainModel? = try {
        GraphQlRequestDomainModel(
            uuid = Uuid.random().toString(),
            infos = FloconGraphQlRequestInfos(
                url = decoded.url!!,
                durationMs = decoded.durationMs!!,
                request = FloconGraphQlRequestInfos.Request(
                    method = decoded.method!!,
                    startTime = decoded.startTime!!,
                    headers = decoded.requestHeaders!!,
                    body = decoded.requestBody,
                    byteSize = decoded.requestSize ?: 0L,
                ),
                response = FloconGraphQlRequestInfos.Response(
                    httpCode = decoded.responseHttpCode!!,
                    contentType = decoded.responseContentType,
                    body = decoded.responseBody,
                    headers = decoded.responseHeaders!!,
                    byteSize = decoded.responseSize ?: 0L,
                ),
            ),
        )
    } catch (t: Throwable) {
        t.printStackTrace()
        null
    }
}
