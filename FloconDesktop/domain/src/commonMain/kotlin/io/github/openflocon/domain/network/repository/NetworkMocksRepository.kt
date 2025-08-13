package io.github.openflocon.domain.network.repository

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import kotlinx.coroutines.flow.Flow

interface NetworkMocksRepository {
    suspend fun addMock(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        mock: MockNetworkDomainModel,
    )

    suspend fun getMock(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        id: String
    ): MockNetworkDomainModel?

    suspend fun getAllEnabledMocks(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ) : List<MockNetworkDomainModel>

    suspend fun observeAll(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ) : Flow<List<MockNetworkDomainModel>>


    suspend fun setupMocks(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        mocks: List<MockNetworkDomainModel>,
    )
    suspend fun deleteMock(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        id: String,
    )

    suspend fun updateMockIsEnabled(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        id: String,
        isEnabled: Boolean,
    )
}
