package io.github.openflocon.flocondesktop.features.network.data

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.FloconIncomingMessageDataModel
import io.github.openflocon.flocondesktop.Protocol
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.features.network.data.datasource.local.NetworkLocalDataSource
import io.github.openflocon.flocondesktop.features.network.data.model.FloconHttpRequestDataModel
import io.github.openflocon.flocondesktop.features.network.data.model.GraphQlRequestBody
import io.github.openflocon.flocondesktop.features.network.data.model.GraphQlResponseBody
import io.github.openflocon.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import io.github.openflocon.flocondesktop.features.network.domain.repository.NetworkImageRepository
import io.github.openflocon.flocondesktop.features.network.domain.repository.NetworkRepository
import io.github.openflocon.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class NetworkRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val networkLocalDataSource: NetworkLocalDataSource,
    private val networkImageRepository: NetworkImageRepository,
) : NetworkRepository,
    MessagesReceiverRepository {
    // maybe inject
    private val httpParser =
        Json {
            ignoreUnknownKeys = true
        }

    override val pluginName = listOf(Protocol.FromDevice.Network.Plugin)

    override fun observeRequests(deviceId: String) = networkLocalDataSource
        .observeRequests(deviceId = deviceId)
        .flowOn(dispatcherProvider.data)

    override fun observeRequest(
        deviceId: String,
        requestId: String,
    ) = networkLocalDataSource
        .observeRequest(
            deviceId = deviceId,
            requestId = requestId,
        ).flowOn(dispatcherProvider.data)

    override suspend fun onMessageReceived(
        deviceId: String,
        message: FloconIncomingMessageDataModel,
    ) {
        withContext(dispatcherProvider.data) {
            decode(message)?.let { toDomain(it) }?.let { request ->
                val responseContentType = request.response.contentType
                if (request.response.contentType != null && responseContentType.startsWith("image/")) {
                    networkImageRepository.onImageReceived(deviceId = deviceId, request = request)
                }
                networkLocalDataSource.save(deviceId = deviceId, request = request)
            }
        }
    }

    private fun decode(message: FloconIncomingMessageDataModel): FloconHttpRequestDataModel? = try {
        httpParser.decodeFromString<FloconHttpRequestDataModel>(message.body)
    } catch (t: Throwable) {
        t.printStackTrace()
        null
    }

    override suspend fun clearDeviceCalls(deviceId: DeviceId) {
        networkLocalDataSource.clearDeviceCalls(deviceId = deviceId)
    }

    override suspend fun deleteRequest(
        deviceId: DeviceId,
        requestId: String,
    ) {
        withContext(dispatcherProvider.data) {
            networkLocalDataSource.deleteRequest(
                deviceId = deviceId,
                requestId = requestId,
            )
        }
    }

    override suspend fun deleteRequestsBefore(deviceId: DeviceId, requestId: String) {
        withContext(dispatcherProvider.data) {
            networkLocalDataSource.deleteRequestsBefore(
                deviceId = deviceId,
                requestId = requestId,
            )
        }
    }

    override suspend fun clear() {
        networkLocalDataSource.clear()
    }

    @OptIn(ExperimentalUuidApi::class)
    fun toDomain(decoded: FloconHttpRequestDataModel): FloconHttpRequestDomainModel? = try {
        val graphQl = extractGraphQl(decoded)
        FloconHttpRequestDomainModel(
            uuid = Uuid.random().toString(),
            url = decoded.url!!,
            durationMs = decoded.durationMs!!,
            request = FloconHttpRequestDomainModel.Request(
                method = decoded.method!!,
                startTime = decoded.startTime!!,
                headers = decoded.requestHeaders!!,
                body = decoded.requestBody,
                byteSize = decoded.requestSize ?: 0L,
            ),
            response = FloconHttpRequestDomainModel.Response(
                httpCode = decoded.responseHttpCode!!,
                contentType = decoded.responseContentType,
                body = decoded.responseBody,
                headers = decoded.responseHeaders!!,
                byteSize = decoded.responseSize ?: 0L,
            ),
            type = when {
                graphQl != null -> FloconHttpRequestDomainModel.Type.GraphQl(graphQl.first.query)
                else -> FloconHttpRequestDomainModel.Type.Http
            }
        )
    } catch (t: Throwable) {
        t.printStackTrace()
        null
    }

    private fun extractGraphQl(decoded: FloconHttpRequestDataModel) : Pair<GraphQlRequestBody, GraphQlResponseBody>? {
        val graphQlQuery = decoded.requestBody?.let {
            try {
                httpParser.decodeFromString<GraphQlRequestBody>(it)
            } catch (t: Throwable) {
                null
            }
        }
        val graphQlResponse = decoded.responseBody?.let {
            try {
                httpParser.decodeFromString<GraphQlResponseBody>(it)
            } catch (t: Throwable) {
                null
            }
        }

        return if(graphQlQuery != null && graphQlResponse != null) {
            graphQlQuery to graphQlResponse
        } else null
    }
}
