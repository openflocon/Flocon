package io.github.openflocon.flocondesktop.features.network.data.datasource.local

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.common.Fakes
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.features.network.data.FloconHttpRequestGenerator
import io.github.openflocon.flocondesktop.features.network.data.datasource.local.mapper.toDomainModel
import io.github.openflocon.flocondesktop.features.network.data.datasource.local.mapper.toEntity
import io.github.openflocon.flocondesktop.features.network.data.datasource.local.model.FloconHttpRequestEntity
import io.github.openflocon.flocondesktop.features.network.data.datasource.local.model.FloconHttpRequestEntityLite
import io.github.openflocon.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NetworkLocalDataSourceRoom(
    private val dispatcherProvider: DispatcherProvider,
    private val floconHttpRequestDao: FloconHttpRequestDao,
    applicationScope: CoroutineScope, // Needed for init Fakes
) : NetworkLocalDataSource {

    init {
        if (Fakes.Enabled) {
            applicationScope.launch(dispatcherProvider.data) {
                val fakeRequests =
                    FloconHttpRequestGenerator.generateDynamicFakeFloconHttpRequests(count = 10)
                fakeRequests.forEach { domainModel ->
                    floconHttpRequestDao.upsertRequest(domainModel.toEntity(Fakes.FakeDeviceId, Fakes.FakePackageName))
                }
            }
        }
    }

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
