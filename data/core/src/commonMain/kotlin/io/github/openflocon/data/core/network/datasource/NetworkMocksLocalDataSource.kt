package io.github.openflocon.data.core.network.datasource

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import kotlinx.coroutines.flow.Flow

interface NetworkMocksLocalDataSource {
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

    suspend fun deleteMock(
        id: String,
    )

    suspend fun updateMockIsEnabled(
        id: String,
        isEnabled: Boolean,
    )

    suspend fun updateMockDevice(
        mockId: String,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel?,
    )
}
