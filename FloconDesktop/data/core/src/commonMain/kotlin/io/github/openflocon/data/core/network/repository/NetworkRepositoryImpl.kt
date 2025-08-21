package io.github.openflocon.data.core.network.repository

import io.github.openflocon.data.core.network.datasource.NetworkLocalDataSource
import io.github.openflocon.data.core.network.datasource.NetworkMocksLocalDataSource
import io.github.openflocon.data.core.network.datasource.NetworkQualityLocalDataSource
import io.github.openflocon.data.core.network.datasource.NetworkRemoteDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import io.github.openflocon.domain.network.models.BadQualityConfigId
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkResponseOnlyDomainModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import io.github.openflocon.domain.network.models.getContentType
import io.github.openflocon.domain.network.repository.NetworkBadQualityRepository
import io.github.openflocon.domain.network.repository.NetworkImageRepository
import io.github.openflocon.domain.network.repository.NetworkMocksRepository
import io.github.openflocon.domain.network.repository.NetworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class NetworkRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val networkLocalDataSource: NetworkLocalDataSource,
    private val networkMocksLocalDataSource: NetworkMocksLocalDataSource,
    private val networkQualityLocalDataSource: NetworkQualityLocalDataSource,
    private val networkImageRepository: NetworkImageRepository,
    private val networkRemoteDataSource: NetworkRemoteDataSource,
) : NetworkRepository,
    NetworkMocksRepository,
    MessagesReceiverRepository,
    NetworkBadQualityRepository {

    override val pluginName = listOf(Protocol.FromDevice.Network.Plugin)

    override suspend fun onDeviceConnected(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        isNewDevice: Boolean,
    ) {
        // on new device, send the mocks setup
        networkRemoteDataSource.setupMocks(
            deviceIdAndPackageName = deviceIdAndPackageName,
            mocks = getAllEnabledMocks(deviceIdAndPackageName = deviceIdAndPackageName),
        )
        if(isNewDevice) {
            networkQualityLocalDataSource.prepopulate(
                deviceIdAndPackageName = deviceIdAndPackageName,
            )
        }
    }

    override fun observeRequests(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        lite: Boolean,
    ): Flow<List<FloconNetworkCallDomainModel>> = networkLocalDataSource
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
                    networkRemoteDataSource.getRequestData(message)
                        ?.let { call ->
                            networkLocalDataSource.save(
                                deviceIdAndPackageName = DeviceIdAndPackageNameDomainModel(
                                    deviceId = deviceId,
                                    packageName = message.appPackageName,
                                ),
                                call = call,
                            )
                        }
                }

                Protocol.FromDevice.Network.Method.LogNetworkCallResponse -> {
                    networkRemoteDataSource.getCallId(message)
                        ?.let {
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

                            val response = networkRemoteDataSource.getResponseData(message) ?: run {
                                println("cannot decode response")
                                return@let null
                            }
                            toDomainForResponse(response = response, request = request)
                        }?.let { call ->
                            if (call.response?.getContentType()?.startsWith("image/") == true) {
                                networkImageRepository.onImageReceived(deviceId = deviceId, call = call)
                            }
                            networkLocalDataSource.save(
                                deviceIdAndPackageName = DeviceIdAndPackageNameDomainModel(
                                    deviceId = deviceId,
                                    packageName = message.appPackageName,
                                ),
                                call = call,
                            )
                        }
                }
            }
            // decode(message)?.let { toDomain(it) }?.let { request ->
            //    val responseContentType = request.response.contentType
            //    if (request.response.contentType != null && responseContentType?.startsWith("image/") == true) {
            //        networkImageRepository.onImageReceived(deviceId = deviceId, request = request)
            //    }
            //    networkLocalDataSource.save(deviceId = deviceId, packageName = message.appPackageName, request = request)
            // }
        }
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
        requestId: String,
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

    fun toDomainForResponse(
        response: FloconNetworkResponseOnlyDomainModel,
        request: FloconNetworkCallDomainModel,
    ): FloconNetworkCallDomainModel? {
        return try {
            val response = when (val r = response.response) {
                is FloconNetworkCallDomainModel.Response.Success -> {
                    // specific case : map to graphQl if needed
                    when (val s = r.specificInfos) {
                        is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Http -> {
                            r.copy(
                                specificInfos = FloconNetworkCallDomainModel.Response.Success.SpecificInfos.GraphQl(
                                    httpCode = s.httpCode,
                                    isSuccess = s.httpCode in 200..299
                                )
                            )
                        }
                        else -> r
                    }
                }

                is FloconNetworkCallDomainModel.Response.Failure -> response.response
            }
            request.copy(
                response = response,
            )
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }
    }

    override suspend fun setupMocks(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        mocks: List<MockNetworkDomainModel>,
    ) {
        withContext(dispatcherProvider.data) {
            networkRemoteDataSource.setupMocks(
                deviceIdAndPackageName = deviceIdAndPackageName,
                mocks = mocks,
            )
        }
    }

    override suspend fun getMock(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        id: String,
    ): MockNetworkDomainModel? = withContext(dispatcherProvider.data) {
        networkMocksLocalDataSource.getMock(
            deviceIdAndPackageName = deviceIdAndPackageName,
            id = id,
        )
    }

    override suspend fun getAllEnabledMocks(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): List<MockNetworkDomainModel> = withContext(dispatcherProvider.data) {
        networkMocksLocalDataSource.getAllEnabledMocks(
            deviceIdAndPackageName = deviceIdAndPackageName,
        )
    }

    override suspend fun addMock(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        mock: MockNetworkDomainModel,
    ) = withContext(dispatcherProvider.data) {
        networkMocksLocalDataSource.addMock(
            deviceIdAndPackageName = deviceIdAndPackageName,
            mock = mock,
        )
    }

    override suspend fun observeAll(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<MockNetworkDomainModel>> =
        networkMocksLocalDataSource.observeAll(
            deviceIdAndPackageName = deviceIdAndPackageName,
        ).flowOn(dispatcherProvider.data)

    override suspend fun deleteMock(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        id: String,
    ) = withContext(dispatcherProvider.data) {
        networkMocksLocalDataSource.deleteMock(
            deviceIdAndPackageName = deviceIdAndPackageName,
            id = id,
        )
    }

    override suspend fun updateMockIsEnabled(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        id: String,
        isEnabled: Boolean,
    ) = withContext(dispatcherProvider.data) {
        networkMocksLocalDataSource.updateMockIsEnabled(
            deviceIdAndPackageName = deviceIdAndPackageName,
            id = id,
            isEnabled = isEnabled,
        )
    }

    override suspend fun setupBadNetworkQuality(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        config: BadQualityConfigDomainModel?
    ) {
        withContext(dispatcherProvider.data) {
            networkRemoteDataSource.setupBadNetworkQuality(
                deviceIdAndPackageName = deviceIdAndPackageName,
                config = config,
            )
        }
    }

    override suspend fun saveBadNetworkQuality(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        config: BadQualityConfigDomainModel
    ) {
        withContext(dispatcherProvider.data) {
            networkQualityLocalDataSource.save(
                deviceIdAndPackageName = deviceIdAndPackageName,
                config = config,
            )
        }
    }

    override suspend fun getNetworkQuality(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        configId: BadQualityConfigId,
    ): BadQualityConfigDomainModel? {
        return withContext(dispatcherProvider.data) {
            networkQualityLocalDataSource.getNetworkQuality(
                deviceIdAndPackageName = deviceIdAndPackageName,
                configId = configId,
            )
        }
    }

    override suspend fun getTheOnlyEnabledNetworkQuality(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): BadQualityConfigDomainModel? {
        return withContext(dispatcherProvider.data) {
            networkQualityLocalDataSource.getTheOnlyEnabledNetworkQuality(
                deviceIdAndPackageName = deviceIdAndPackageName,
            )
        }
    }

    override suspend fun deleteNetworkQuality(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        configId: BadQualityConfigId,
    ) {
        return withContext(dispatcherProvider.data) {
            networkQualityLocalDataSource.delete(
                deviceIdAndPackageName = deviceIdAndPackageName,
                configId = configId,
            )
        }
    }

    override fun observeNetworkQuality(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        configId: BadQualityConfigId,
    ): Flow<BadQualityConfigDomainModel?> {
        return networkQualityLocalDataSource.observe(
            deviceIdAndPackageName = deviceIdAndPackageName,
            configId = configId,
        ).flowOn(dispatcherProvider.data)
    }

    override fun observeAllNetworkQualities(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<BadQualityConfigDomainModel>> {
        return networkQualityLocalDataSource.observeAll(
            deviceIdAndPackageName = deviceIdAndPackageName,
        ).flowOn(dispatcherProvider.data)
    }

    override suspend fun setEnabledConfig(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        configId: BadQualityConfigId?,
    ) {
        withContext(dispatcherProvider.data) {
            networkQualityLocalDataSource.setEnabledConfig(
                deviceIdAndPackageName = deviceIdAndPackageName,
                configId = configId,
            )
        }
    }

}
