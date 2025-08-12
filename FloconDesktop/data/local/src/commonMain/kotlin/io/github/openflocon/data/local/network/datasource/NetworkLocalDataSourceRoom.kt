package io.github.openflocon.data.local.network.datasource

import io.github.openflocon.data.core.network.datasource.NetworkLocalDataSource
import io.github.openflocon.data.local.network.dao.FloconNetworkDao
import io.github.openflocon.data.local.network.mapper.toDomainModel
import io.github.openflocon.data.local.network.mapper.toEntity
import io.github.openflocon.data.local.network.models.FloconNetwockCallEntityLite
import io.github.openflocon.data.local.network.models.FloconNetworkCallEntity
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class NetworkLocalDataSourceRoom(
    private val dispatcherProvider: DispatcherProvider,
    private val floconNetworkDao: FloconNetworkDao
) : NetworkLocalDataSource {

    override fun observeRequests(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        lite: Boolean,
    ): Flow<List<FloconNetworkCallDomainModel>> = floconNetworkDao.let {
        if (lite) {
            it.observeRequestsLite(
                deviceId = deviceIdAndPackageName.deviceId,
                packageName = deviceIdAndPackageName.packageName,
            )
                .map { entities -> entities.mapNotNull(FloconNetwockCallEntityLite::toDomainModel) }
        } else {
            it.observeRequests(
                deviceId = deviceIdAndPackageName.deviceId,
                packageName = deviceIdAndPackageName.packageName,
            )
                .map { entities -> entities.mapNotNull(FloconNetworkCallEntity::toDomainModel) }
        }
    }
        .flowOn(dispatcherProvider.data)

    override suspend fun save(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        call: FloconNetworkCallDomainModel,
    ) {
        withContext(dispatcherProvider.data) {
            val entity = call.toEntity(
                deviceId = deviceIdAndPackageName.deviceId,
                packageName = deviceIdAndPackageName.packageName,
            )
            floconNetworkDao.upsertRequest(entity)
        }
    }

    override suspend fun getCall(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        callId: String,
    ): FloconNetworkCallDomainModel? = withContext(dispatcherProvider.data) {
        floconNetworkDao
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
        }.flowOn(dispatcherProvider.data)

    override suspend fun clearDeviceCalls(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        withContext(dispatcherProvider.data) {
            floconNetworkDao.clearDeviceCalls(
                deviceId = deviceIdAndPackageName.deviceId,
                packageName = deviceIdAndPackageName.packageName,
            )
        }
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
        withContext(dispatcherProvider.data) {
            floconNetworkDao.clearAll()
        }
    }
}
