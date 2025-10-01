package io.github.openflocon.data.local.network.datasource

import io.github.openflocon.data.core.network.datasource.NetworkMocksLocalDataSource
import io.github.openflocon.data.local.network.dao.NetworkMocksDao
import io.github.openflocon.data.local.network.mapper.toDomain
import io.github.openflocon.data.local.network.mapper.toEntity
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class NetworkMocksLocalDataSourceImpl(
    private val dao: NetworkMocksDao,
    private val json: Json,
) : NetworkMocksLocalDataSource {

    override suspend fun addMock(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        mock: MockNetworkDomainModel,
    ) {
        mock.toEntity(
            json = json,
            deviceInfo = deviceIdAndPackageName
        )?.let {
            dao.addMock(it)
        }
    }

    override suspend fun getMock(
        id: String
    ): MockNetworkDomainModel? {
        return dao.getMock(
            mockId = id
        )?.toDomain(
            json = json,
        )
    }

    override suspend fun getAllEnabledMocks(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): List<MockNetworkDomainModel> {
        return dao.getAllEnabledMocks(
            deviceIdAndPackageName.deviceId,
            deviceIdAndPackageName.packageName
        ).mapNotNull {
            it.toDomain(
                json = json,
            )
        }
    }

    override suspend fun observeAll(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<List<MockNetworkDomainModel>> {
        return dao.observeAllMocks(
            deviceIdAndPackageName.deviceId,
            deviceIdAndPackageName.packageName
        ).map { entities ->
            entities.mapNotNull {
                it.toDomain(
                    json = json,
                )
            }
        }
    }

    override suspend fun deleteMock(
        id: String,
    ) {
        dao.deleteMock(
            mockId = id,
        )
    }

    override suspend fun updateMockIsEnabled(
        id: String,
        isEnabled: Boolean
    ) {
        dao.updateMockIsEnabled(
            mockId = id,
            isEnabled = isEnabled,
        )
    }

    override suspend fun updateMockDevice(
        mockId: String,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel?
    ) {
        dao.updateMockDevice(
            mockId = mockId,
            deviceId = deviceIdAndPackageName?.deviceId,
            packageName = deviceIdAndPackageName?.packageName,
        )
    }
}
