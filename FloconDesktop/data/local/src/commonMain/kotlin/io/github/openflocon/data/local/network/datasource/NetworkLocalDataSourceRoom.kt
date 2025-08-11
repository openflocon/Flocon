package io.github.openflocon.data.local.network.datasource

import io.github.openflocon.data.core.network.datasource.NetworkLocalDataSource
import io.github.openflocon.data.local.network.dao.FloconHttpRequestDao
import io.github.openflocon.data.local.network.mapper.toDomainModel
import io.github.openflocon.data.local.network.mapper.toEntity
import io.github.openflocon.data.local.network.models.FloconHttpRequestEntity
import io.github.openflocon.data.local.network.models.FloconHttpRequestEntityLite
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.FloconHttpRequestDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class NetworkLocalDataSourceRoom(
    private val dispatcherProvider: DispatcherProvider,
    private val floconHttpRequestDao: FloconHttpRequestDao
) : NetworkLocalDataSource {

    override fun observeRequests(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        lite: Boolean,
    ): Flow<List<FloconHttpRequestDomainModel>> = floconHttpRequestDao.let {
        if (lite) {
            it.observeRequestsLite(
                deviceId = deviceIdAndPackageName.deviceId,
                packageName = deviceIdAndPackageName.packageName,
            )
                .map { entities -> entities.mapNotNull(FloconHttpRequestEntityLite::toDomainModel) }
        } else {
            it.observeRequests(
                deviceId = deviceIdAndPackageName.deviceId,
                packageName = deviceIdAndPackageName.packageName,
            )
                .map { entities -> entities.mapNotNull(FloconHttpRequestEntity::toDomainModel) }
        }
    }
        .flowOn(dispatcherProvider.data)

    override suspend fun save(
        deviceId: DeviceId,
        packageName: String,
        request: FloconHttpRequestDomainModel,
    ) {
        withContext(dispatcherProvider.data) {
            val entity = request.toEntity(deviceId = deviceId, packageName = packageName)
            floconHttpRequestDao.upsertRequest(entity)
        }
    }

    override fun observeRequest(
        deviceId: DeviceId,
        requestId: String,
    ): Flow<FloconHttpRequestDomainModel?> = floconHttpRequestDao
        .observeRequestById(deviceId, requestId)
        .map { entity ->
            entity?.toDomainModel()
        }.flowOn(dispatcherProvider.data)

    override suspend fun clearDeviceCalls(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        withContext(dispatcherProvider.data) {
            floconHttpRequestDao.clearDeviceCalls(
                deviceId = deviceIdAndPackageName.deviceId,
                packageName = deviceIdAndPackageName.packageName,
            )
        }
    }

    override suspend fun deleteRequest(
        deviceId: DeviceId,
        requestId: String,
    ) {
        floconHttpRequestDao.deleteRequest(
            requestId = requestId,
            deviceId = deviceId,
        )
    }

    override suspend fun deleteRequestsBefore(deviceId: DeviceId, requestId: String) {
        floconHttpRequestDao.deleteRequestBefore(
            requestId = requestId,
            deviceId = deviceId,
        )
    }

    override suspend fun clear() {
        withContext(dispatcherProvider.data) {
            floconHttpRequestDao.clearAll()
        }
    }
}
