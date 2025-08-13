package io.github.openflocon.data.local.network.datasource

import io.github.openflocon.data.core.network.datasource.NetworkMocksLocalDataSource
import io.github.openflocon.data.local.network.dao.NetworkMocksDao
import io.github.openflocon.data.local.network.mapper.toDomain
import io.github.openflocon.data.local.network.mapper.toEntity
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NetworkMocksLocalDataSourceImpl(
    private val dao: NetworkMocksDao,
) : NetworkMocksLocalDataSource {

    override suspend fun addMock(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        mock: MockNetworkDomainModel,
    ) {
        val mockEntity = toEntity(mock, deviceIdAndPackageName)
        dao.addMock(mockEntity)
    }

    override suspend fun getMock(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        id: String
    ): MockNetworkDomainModel? {
        return dao.getMock(
            deviceIdAndPackageName.deviceId,
            deviceIdAndPackageName.packageName,
            id
        )?.let {
            toDomain(it)
        }
    }

    override suspend fun getAllEnabledMocks(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): List<MockNetworkDomainModel> {
        return dao.getAllEnabledMocks(
            deviceIdAndPackageName.deviceId,
            deviceIdAndPackageName.packageName
        ).map {
            toDomain(it)
        }
    }

    override suspend fun observeAll(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<List<MockNetworkDomainModel>> {
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
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            mockId = id,
        )
    }

    override suspend fun updateMockIsEnabled(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        id: String,
        isEnabled: Boolean
    ) {
        dao.updateMockIsEnabled(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            mockId = id,
            isEnabled = isEnabled,
        )
    }
}
