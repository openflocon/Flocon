package com.florent37.flocondesktop.features.network.data

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.FloconIncomingMessageDataModel
import com.florent37.flocondesktop.Protocol
import com.florent37.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import com.florent37.flocondesktop.features.network.data.datasource.local.NetworkLocalDataSource
import com.florent37.flocondesktop.features.network.data.model.FloconHttpRequestDataModel
import com.florent37.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import com.florent37.flocondesktop.features.network.domain.model.FloconHttpRequestInfos
import com.florent37.flocondesktop.features.network.domain.repository.NetworkImageRepository
import com.florent37.flocondesktop.features.network.domain.repository.NetworkRepository
import com.florent37.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
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
                val responseContentType = request.infos.response.contentType
                if (request.infos.response.contentType != null && responseContentType.startsWith("image/")) {
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
        FloconHttpRequestDomainModel(
            uuid = Uuid.random().toString(),
            infos =
            FloconHttpRequestInfos(
                url = decoded.url!!,
                method = decoded.method!!,
                startTime = decoded.startTime!!,
                durationMs = decoded.durationMs!!,
                request = FloconHttpRequestInfos.Request(
                    headers = decoded.requestHeaders!!,
                    body = decoded.requestBody,
                    byteSize = decoded.requestSize ?: 0L,
                ),
                response = FloconHttpRequestInfos.Response(
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
