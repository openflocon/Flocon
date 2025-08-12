package io.github.openflocon.data.local.network.datasource

import io.github.openflocon.data.core.network.datasource.NetworkMocksLocalDataSource
import io.github.openflocon.data.local.network.dao.NetworkMocksDao
import io.github.openflocon.data.local.network.mapper.toDomain
import io.github.openflocon.data.local.network.mapper.toEntity
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.MockNetworkResponseDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NetworkMocksLocalDataSourceImpl(
    private val dao: NetworkMocksDao,
) : NetworkMocksLocalDataSource {

    override suspend fun addMock(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        mock: MockNetworkResponseDomainModel,
    ) {
        val mockEntity = toEntity(mock, deviceIdAndPackageName)
        dao.addMock(mockEntity)
    }

    override suspend fun getAllMocks(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): List<MockNetworkResponseDomainModel> {
        return dao.getAllMocks(
            deviceIdAndPackageName.deviceId,
            deviceIdAndPackageName.packageName
        ).map {
            toDomain(it)
        }
    }

    override suspend fun observeAll(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<List<MockNetworkResponseDomainModel>> {
        return dao.observeAllMocks(
            deviceIdAndPackageName.deviceId,
            deviceIdAndPackageName.packageName
        ).map { entities ->
            entities.map {
                toDomain(it)
            }
        }
    }

    override suspend fun deleteMock(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        id: String,
    ) {
        dao.deleteMock(
            deviceIdAndPackageName.deviceId,
            deviceIdAndPackageName.packageName,
            id
        )
    }
}
