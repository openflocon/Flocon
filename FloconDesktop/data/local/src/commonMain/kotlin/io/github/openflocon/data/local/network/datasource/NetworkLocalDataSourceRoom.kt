package io.github.openflocon.data.local.network.datasource

import io.github.openflocon.data.core.network.datasource.NetworkLocalDataSource
import io.github.openflocon.data.local.network.dao.FloconNetworkDao
import io.github.openflocon.data.local.network.mapper.toDomainModel
import io.github.openflocon.data.local.network.mapper.toEntity
import io.github.openflocon.data.local.network.models.FloconNetworkCallEntity
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class NetworkLocalDataSourceRoom(
    private val floconNetworkDao: FloconNetworkDao,
) : NetworkLocalDataSource {

    override fun observeRequests(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<List<FloconNetworkCallDomainModel>> = floconNetworkDao.let {
        it.observeRequests(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
        ).map { entities -> entities.mapNotNull(FloconNetworkCallEntity::toDomainModel) }
    }

    override suspend fun getCalls(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        ids: List<String>
    ): List<FloconNetworkCallDomainModel> {
        return floconNetworkDao.getRequests(
            ids = ids,
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
        ).mapNotNull { entities -> entities.toDomainModel() }
    }

    override suspend fun save(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        call: FloconNetworkCallDomainModel,
    ) {
        val entity = call.toEntity(
            deviceIdAndPackageName = deviceIdAndPackageName,
        )
        floconNetworkDao.upsertRequest(entity)
    }

    override suspend fun getCall(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        callId: String,
    ): FloconNetworkCallDomainModel?  {
        return floconNetworkDao
            .getCallById(
                deviceId = deviceIdAndPackageName.deviceId,
                packageName = deviceIdAndPackageName.packageName,
                callId = callId,
            )
            ?.toDomainModel()
    }

    override fun observeCall(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        callId: String,
    ): Flow<FloconNetworkCallDomainModel?> = floconNetworkDao
        .observeCallById(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            callId = callId,
        )
        .map { entity ->
            entity?.toDomainModel()
        }

    override suspend fun clearDeviceCalls(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        floconNetworkDao.clearDeviceCalls(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
        )
    }

    override suspend fun deleteRequest(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        callId: String,
    ) {
        floconNetworkDao.deleteRequest(
            callId = callId,
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
        )
    }

    override suspend fun deleteRequestOnDifferentSession(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ) {
        floconNetworkDao.deleteRequestOnDifferentSession(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            appInstance = deviceIdAndPackageName.appInstance
        )
    }

    override suspend fun deleteRequestsBefore(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        callId: String
    ) {
        floconNetworkDao.deleteRequestBefore(
            callId = callId,
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
        )
    }

    override suspend fun clear() {
        floconNetworkDao.clearAll()
    }
}
