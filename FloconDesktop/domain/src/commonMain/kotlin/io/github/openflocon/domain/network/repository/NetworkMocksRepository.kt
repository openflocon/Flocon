package io.github.openflocon.domain.network.repository

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import io.github.openflocon.domain.network.models.NetworkWebsocketId
import kotlinx.coroutines.flow.Flow

interface NetworkMocksRepository {
    suspend fun addMock(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        mock: MockNetworkDomainModel,
    )

    suspend fun getMock(
        id: String
    ): MockNetworkDomainModel?

    suspend fun getAllEnabledMocks(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): List<MockNetworkDomainModel>

    suspend fun observeAll(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<List<MockNetworkDomainModel>>

    suspend fun setupMocks(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        mocks: List<MockNetworkDomainModel>,
    )
    suspend fun deleteMock(
        id: String,
    )

    suspend fun updateMockIsEnabled(
        id: String,
        isEnabled: Boolean,
    )

    suspend fun updateMockDevice(
        mockId: String,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel?
    )

    suspend fun observeWebsocketClientsIds(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<List<NetworkWebsocketId>>

    suspend fun sendWebsocketMock(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        websocketId: NetworkWebsocketId,
        message: String,
    )
}
