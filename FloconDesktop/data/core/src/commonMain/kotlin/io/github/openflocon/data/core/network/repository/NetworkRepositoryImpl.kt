package io.github.openflocon.data.core.network.repository

import co.touchlab.kermit.Logger
import io.github.openflocon.data.core.network.datasource.NetworkLocalDataSource
import io.github.openflocon.data.core.network.datasource.NetworkLocalWebsocketDataSource
import io.github.openflocon.data.core.network.datasource.NetworkMocksLocalDataSource
import io.github.openflocon.data.core.network.datasource.NetworkQualityLocalDataSource
import io.github.openflocon.data.core.network.datasource.NetworkRemoteDataSource
import io.github.openflocon.data.core.network.datasource.NetworkSettingsLocalDataSource
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
import io.github.openflocon.domain.network.models.NetworkFilterDomainModel
import io.github.openflocon.domain.network.models.NetworkSettingsDomainModel
import io.github.openflocon.domain.network.models.NetworkSortDomainModel
import io.github.openflocon.domain.network.models.NetworkWebsocketId
import io.github.openflocon.domain.network.models.isImage
import io.github.openflocon.domain.network.repository.NetworkBadQualityRepository
import io.github.openflocon.domain.network.repository.NetworkImageRepository
import io.github.openflocon.domain.network.repository.NetworkMocksRepository
import io.github.openflocon.domain.network.repository.NetworkRepository
import io.github.openflocon.domain.network.repository.NetworkSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class NetworkRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val networkLocalDataSource: NetworkLocalDataSource,
    private val networkLocalWebsocketDataSource: NetworkLocalWebsocketDataSource,
    private val networkMocksLocalDataSource: NetworkMocksLocalDataSource,
    private val networkQualityLocalDataSource: NetworkQualityLocalDataSource,
    private val networkSettingsLocalDataSource: NetworkSettingsLocalDataSource,
    private val networkImageRepository: NetworkImageRepository,
    private val networkRemoteDataSource: NetworkRemoteDataSource,
) : NetworkRepository,
    NetworkMocksRepository,
    MessagesReceiverRepository,
    NetworkSettingsRepository,
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
        sortedBy: NetworkSortDomainModel?,
        filter: NetworkFilterDomainModel,
    ) = networkLocalDataSource
        .observeRequests(
            deviceIdAndPackageName = deviceIdAndPackageName,
            sortedBy = sortedBy,
            filter = filter,
        )
        .flowOn(dispatcherProvider.data)

    override suspend fun getRequests(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sortedBy: NetworkSortDomainModel?,
        filter: NetworkFilterDomainModel,
    ): List<FloconNetworkCallDomainModel> = withContext(dispatcherProvider.data) {
        networkLocalDataSource
            .getRequests(
                deviceIdAndPackageName = deviceIdAndPackageName,
                sortedBy = sortedBy,
                filter = filter,
            )
    }

    override suspend fun getRequests(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        ids: List<String>
    ): List<FloconNetworkCallDomainModel> {
        return withContext(dispatcherProvider.data) {
            networkLocalDataSource.getCalls(
                deviceIdAndPackageName = deviceIdAndPackageName,
                ids = ids,
            )
        }
    }

    override fun observeRequest(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        requestId: String,
    ) = networkLocalDataSource
        .observeCall(
            deviceIdAndPackageName = deviceIdAndPackageName,
            callId = requestId,
        ).flowOn(dispatcherProvider.data)

    override suspend fun onMessageReceived(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        message: FloconIncomingMessageDomainModel
    ) {
        withContext(dispatcherProvider.data) {
            when (message.method) {
                Protocol.FromDevice.Network.Method.RegisterWebSocketIds -> {
                    networkLocalWebsocketDataSource.registerWebsocketClients(
                        deviceIdAndPackageName = deviceIdAndPackageName,
                        ids = networkRemoteDataSource.getWebsocketClientsIds(message),
                    )
                }
                Protocol.FromDevice.Network.Method.LogWebSocketEvent -> {
                    networkRemoteDataSource.getWebSocketData(message)
                        ?.let { wenSocketEvent ->
                            networkLocalDataSource.save(
                                deviceIdAndPackageName = deviceIdAndPackageName,
                                call = wenSocketEvent,
                            )
                        }
                }

                Protocol.FromDevice.Network.Method.LogNetworkCallRequest -> {
                    networkRemoteDataSource.getRequestData(message)
                        ?.let { call ->
                            networkLocalDataSource.save(
                                deviceIdAndPackageName = deviceIdAndPackageName,
                                call = call,
                            )
                        }
                }

                Protocol.FromDevice.Network.Method.LogNetworkCallResponse -> {
                    networkRemoteDataSource.getCallId(message)
                        ?.let {
                            val callId = it.floconCallId ?: run {
                                Logger.e { "cannot find floconCallId in message" }
                                return@let null
                            }
                            val request = networkLocalDataSource.getCall(
                                deviceIdAndPackageName = deviceIdAndPackageName,
                                callId = callId,
                            ) ?: run {
                                Logger.e { "cannot find request" }
                                return@let null
                            }

                            val response = networkRemoteDataSource.getResponseData(message) ?: run {
                                Logger.e { "cannot decode response" }
                                return@let null
                            }
                            toDomainForResponse(receivedResponse = response, request = request)
                        }?.let { call ->
                            if (call.response?.isImage() == true) {
                                networkImageRepository.onImageReceived(deviceIdAndPackageName = deviceIdAndPackageName, call = call)
                            }
                            networkLocalDataSource.save(
                                deviceIdAndPackageName = deviceIdAndPackageName,
                                call = call,
                            )
                        }
                }
            }
        }
    }

    override suspend fun clearDeviceCalls(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        withContext(dispatcherProvider.data) {
            networkLocalDataSource.clearDeviceCalls(deviceIdAndPackageName = deviceIdAndPackageName)
        }
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
        withContext(dispatcherProvider.data) {
            networkLocalDataSource.clear()
        }
    }

    fun toDomainForResponse(
        receivedResponse: FloconNetworkResponseOnlyDomainModel,
        request: FloconNetworkCallDomainModel,
    ): FloconNetworkCallDomainModel? {
        val isRequestGraphQl = request.request.specificInfos is FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl
        return try {
            val response = if(isRequestGraphQl) {
                when (val r = receivedResponse.response) {
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

                    is FloconNetworkCallDomainModel.Response.Failure -> receivedResponse.response
                }
            } else {
                receivedResponse.response
            }

            request.copy(
                request = request.request.copy(
                    headers = receivedResponse.toUpdateRequestHeaders ?: request.request.headers,
                ),
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
        id: String,
    ): MockNetworkDomainModel? = withContext(dispatcherProvider.data) {
        networkMocksLocalDataSource.getMock(
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
        id: String,
    ) = withContext(dispatcherProvider.data) {
        networkMocksLocalDataSource.deleteMock(
            id = id,
        )
    }

    override suspend fun updateMockIsEnabled(
        id: String,
        isEnabled: Boolean,
    ) = withContext(dispatcherProvider.data) {
        networkMocksLocalDataSource.updateMockIsEnabled(
            id = id,
            isEnabled = isEnabled,
        )
    }

    override suspend fun updateMockDevice(
        mockId: String,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel?,
    ) {
        withContext(dispatcherProvider.data) {
            networkMocksLocalDataSource.updateMockDevice(
                mockId = mockId,
                deviceIdAndPackageName = deviceIdAndPackageName,
            )
        }
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

    override suspend fun deleteOldRequests(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        withContext(dispatcherProvider.data) {
            networkLocalDataSource.deleteRequestOnDifferentSession(
                deviceIdAndPackageName = deviceIdAndPackageName,
            )
        }
    }

    override suspend fun getNetworkSettings(deviceAndApp: DeviceIdAndPackageNameDomainModel): NetworkSettingsDomainModel? {
        return withContext(dispatcherProvider.data) {
            networkSettingsLocalDataSource.getNetworkSettings(
                deviceAndApp = deviceAndApp,
            )
        }
    }

    override fun observeNetworkSettings(deviceAndApp: DeviceIdAndPackageNameDomainModel): Flow<NetworkSettingsDomainModel?> {
        return networkSettingsLocalDataSource.observeNetworkSettings(
            deviceAndApp = deviceAndApp,
        ).flowOn(dispatcherProvider.data)
    }

    override suspend fun updateNetworkSettings(
        deviceAndApp: DeviceIdAndPackageNameDomainModel,
        newValue: NetworkSettingsDomainModel
    ) {
        withContext(dispatcherProvider.data) {
            networkSettingsLocalDataSource.updateNetworkSettings(
                deviceAndApp = deviceAndApp,
                newValue = newValue,
            )
        }
    }

    override suspend fun observeWebsocketClientsIds(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<NetworkWebsocketId>> {
        return networkLocalWebsocketDataSource.observeWebsocketClients(
            deviceIdAndPackageName = deviceIdAndPackageName,
        ).flowOn(dispatcherProvider.data)
    }

    override suspend fun sendWebsocketMock(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        websocketId: NetworkWebsocketId,
        message: String
    ) {
        withContext(dispatcherProvider.data) {
            networkRemoteDataSource.sendWebsocketMock(
                deviceIdAndPackageName = deviceIdAndPackageName,
                websocketId = websocketId,
                message = message,
            )
        }
    }

}
