package io.github.openflocon.data.core.network.datasource

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.MockNetworkResponseDomainModel
import kotlinx.coroutines.flow.Flow

interface NetworkMocksLocalDataSource {
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

    suspend fun deleteMock(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        id: String,
    )
}
