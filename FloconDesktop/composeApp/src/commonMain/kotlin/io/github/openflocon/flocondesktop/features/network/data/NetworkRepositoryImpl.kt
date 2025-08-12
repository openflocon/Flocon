package io.github.openflocon.flocondesktop.features.network.data

import io.github.openflocon.data.core.network.datasource.NetworkLocalDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkRequestDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkResponseDomainModel
import io.github.openflocon.domain.network.repository.NetworkImageRepository
import io.github.openflocon.domain.network.repository.NetworkRepository
import io.github.openflocon.flocondesktop.features.network.data.model.FloconNetworkCallIdDataModel
import io.github.openflocon.flocondesktop.features.network.data.model.FloconNetworkRequestDataModel
import io.github.openflocon.flocondesktop.features.network.data.model.FloconNetworkResponseDataModel
import io.github.openflocon.flocondesktop.features.network.data.parser.graphql.extractGraphQl
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
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        requestId: String,
    ) = networkLocalDataSource
        .observeCall(
            deviceIdAndPackageName = deviceIdAndPackageName,
            callId = requestId,
        ).flowOn(dispatcherProvider.data)

    override suspend fun onMessageReceived(deviceId: String, message: FloconIncomingMessageDomainModel) {
        withContext(dispatcherProvider.data) {
            when (message.method) {
                Protocol.FromDevice.Network.Method.LogNetworkCallRequest -> {
                    decodeRequest(message)?.let { toDomain(it) }?.let { call ->
                        networkLocalDataSource.save(
                            deviceIdAndPackageName = DeviceIdAndPackageNameDomainModel(
                                deviceId = deviceId,
                                packageName = message.appPackageName,
                            ),
                            call = call
                        )
                    }
                }

                Protocol.FromDevice.Network.Method.LogNetworkCallResponse -> {
                    decodeResponseCallId(message)?.let {
                        val callId = it.floconCallId ?: run {
                            println("cannot find floconCallId in message")
                            return@let null
                        }
                        val request = networkLocalDataSource.getCall(
                            deviceIdAndPackageName = DeviceIdAndPackageNameDomainModel(
                                deviceId = deviceId,
                                packageName = message.appPackageName,
                            ),
                            callId = callId,
                        ) ?: run {
                            println("cannot find request")
                            return@let null
                        }

                        val response = decodeResponse(message) ?: run {
                            println("cannot decide response")
                            return@let null
                        }
                        toDomainForResponse(decoded = response, request = request)
                    }?.let { call ->
                        if (call.networkResponse?.contentType?.startsWith("image/") == true) {
                            networkImageRepository.onImageReceived(deviceId = deviceId, call = call)
                        }
                        networkLocalDataSource.save(
                            deviceIdAndPackageName = DeviceIdAndPackageNameDomainModel(
                                deviceId = deviceId,
                                packageName = message.appPackageName,
                            ),
                            call = call
                        )
                    }
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

    private fun decodeRequest(message: FloconIncomingMessageDomainModel): FloconNetworkRequestDataModel? =
        try {
            httpParser.decodeFromString<FloconNetworkRequestDataModel>(message.body)
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }

    private fun decodeResponseCallId(message: FloconIncomingMessageDomainModel): FloconNetworkCallIdDataModel? =
        try {
            httpParser.decodeFromString<FloconNetworkCallIdDataModel>(message.body)
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }

    private fun decodeResponse(message: FloconIncomingMessageDomainModel): FloconNetworkResponseDataModel? =
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
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        requestId: String,
    ) {
        withContext(dispatcherProvider.data) {
            networkLocalDataSource.deleteRequest(
                deviceIdAndPackageName = deviceIdAndPackageName,
                callId = requestId,
            )
        }
    }

    override suspend fun deleteRequestsBefore(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        requestId: String
    ) {
        withContext(dispatcherProvider.data) {
            networkLocalDataSource.deleteRequestsBefore(
                deviceIdAndPackageName = deviceIdAndPackageName,
                callId = requestId,
            )
        }
    }

    override suspend fun clear() {
        networkLocalDataSource.clear()
    }

    fun toDomainForResponse(decoded: FloconNetworkResponseDataModel, request: FloconNetworkCallDomainModel): FloconNetworkCallDomainModel? {
        try {
            val networkResponse = FloconNetworkResponseDomainModel(
                contentType = decoded.responseContentType,
                body = decoded.responseBody,
                headers = decoded.responseHeaders ?: emptyMap(),
                byteSize = decoded.responseSize ?: 0L,
                durationMs = decoded.durationMs!!,
            )

            return when (request) {
                is FloconNetworkCallDomainModel.GraphQl -> {
                    val response = FloconNetworkCallDomainModel.GraphQl.Response(
                        networkResponse = networkResponse,
                        httpCode = decoded.responseHttpCode!!,
                        isSuccess = decoded.responseGrpcStatus == "OK",
                    )
                    request.copy(
                        response = response
                    )
                }

                is FloconNetworkCallDomainModel.Grpc -> {
                    val response = FloconNetworkCallDomainModel.Grpc.Response(
                        networkResponse = networkResponse,
                        responseStatus = decoded.responseGrpcStatus!!,
                    )
                    request.copy(
                        response = response
                    )
                }

                is FloconNetworkCallDomainModel.Http -> {
                    val response = FloconNetworkCallDomainModel.Http.Response(
                        networkResponse = networkResponse,
                        httpCode = decoded.responseHttpCode!!,
                    )
                    request.copy(
                        response = response
                    )
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            return null
        }
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
