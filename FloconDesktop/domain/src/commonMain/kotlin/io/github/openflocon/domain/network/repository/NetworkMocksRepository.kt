package io.github.openflocon.domain.network.repository

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.MockNetworkResponseDomainModel
import kotlinx.coroutines.flow.Flow

interface NetworkMocksRepository {
    suspend fun addMock(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        mock: MockNetworkResponseDomainModel,
    )

    suspend fun getAllMocks(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ) : List<MockNetworkResponseDomainModel>

    suspend fun observeAll(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ) : Flow<List<MockNetworkResponseDomainModel>>


    suspend fun setupMocks(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        mocks: List<MockNetworkResponseDomainModel>,
    )
    suspend fun deleteMock(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        id: String,
    )
}
