package io.github.openflocon.flocondesktop.features.network.data

import com.flocon.data.remote.Protocol
import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import io.github.openflocon.data.core.network.datasource.NetworkLocalDataSource
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkRequestDomainModel
import io.github.openflocon.domain.network.repository.NetworkImageRepository
import io.github.openflocon.domain.network.repository.NetworkRepository
import io.github.openflocon.flocondesktop.features.network.data.model.FloconNetworkRequestDataModel
import io.github.openflocon.flocondesktop.features.network.data.model.FloconNetworkResponseDataModel
import io.github.openflocon.flocondesktop.features.network.data.parser.graphql.extractGraphQl
import io.github.openflocon.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi

class NetworkRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val networkLocalDataSource: NetworkLocalDataSource,
    private val networkImageRepository: NetworkImageRepository,
) : NetworkRepository,
    MessagesReceiverRepository {
    // maybe inject
    private val httpParser = Json {
        ignoreUnknownKeys = true
    }

    override val pluginName = listOf(Protocol.FromDevice.Network.Plugin)

    override fun observeRequests(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        lite: Boolean
    ): Flow<List<FloconNetworkCallDomainModel>> =
        networkLocalDataSource
            .observeRequests(deviceIdAndPackageName = deviceIdAndPackageName, lite = lite)
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
            when (message.method) {
                Protocol.FromDevice.Network.Method.LogNetworkCallRequest -> {
                    decodeRequest(message)?.let { toDomain(it) }?.let { call ->
                        networkLocalDataSource.save(
                            deviceId = deviceId,
                            packageName = message.appPackageName,
                            call = call
                        )
                    }
                }

                Protocol.FromDevice.Network.Method.LogNetworkCallResponse -> {
                    /*
                    decodeResponse(message)?.let { toDomain(it) }?.let { response ->
                        val request: FloconNetworkCallDomainModel? =
                            TODO() ?: return@let // find by callId
                        val callWithResponse = // inject response
                            networkLocalDataSource.save(
                                deviceId = deviceId,
                                packageName = message.appPackageName,
                                request = request
                            )
                    }
                     */
                }
            }
            //decode(message)?.let { toDomain(it) }?.let { request ->
            //    val responseContentType = request.response.contentType
            //    if (request.response.contentType != null && responseContentType?.startsWith("image/") == true) {
            //        networkImageRepository.onImageReceived(deviceId = deviceId, request = request)
            //    }
            //    networkLocalDataSource.save(deviceId = deviceId, packageName = message.appPackageName, request = request)
            //}
        }
    }

    private fun decodeRequest(message: FloconIncomingMessageDataModel): FloconNetworkRequestDataModel? =
        try {
            httpParser.decodeFromString<FloconNetworkRequestDataModel>(message.body)
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }

    private fun decodeResponse(message: FloconIncomingMessageDataModel): FloconNetworkResponseDataModel? =
        try {
            httpParser.decodeFromString<FloconNetworkResponseDataModel>(message.body)
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }

    override suspend fun clearDeviceCalls(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        networkLocalDataSource.clearDeviceCalls(deviceIdAndPackageName = deviceIdAndPackageName)
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
    fun toDomain(decoded: FloconNetworkRequestDataModel): FloconNetworkCallDomainModel? = try {
        val graphQl = extractGraphQl(decoded)

        val callId = decoded.floconCallId!!
        val networkRequest = FloconNetworkRequestDomainModel(
            url = decoded.url!!,
            startTime = decoded.startTime!!,
            method = decoded.method!!,
            headers = decoded.requestHeaders!!,
            body = decoded.requestBody,
            byteSize = decoded.requestSize ?: 0L,
        )

        when {
            graphQl != null -> FloconNetworkCallDomainModel.GraphQl(
                callId = callId,
                request = FloconNetworkCallDomainModel.GraphQl.Request(
                    query = graphQl.request.queryName ?: "anonymous",
                    operationType = graphQl.request.operationType,
                    networkRequest = networkRequest,
                ),
                response = null,
            )

            decoded.floconNetworkType == "grpc" -> FloconNetworkCallDomainModel.Grpc(
                callId = callId,
                networkRequest = networkRequest,
                response = null,
            )
            // decoded.floconNetworkType == "http"
            else -> {
                FloconNetworkCallDomainModel.Http(
                    callId = callId,
                    networkRequest = networkRequest,
                    response = null,
                )
            }
        }
    } catch (t: Throwable) {
        t.printStackTrace()
        null
    }
}
