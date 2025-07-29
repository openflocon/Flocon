package com.florent37.flocondesktop.features.network.data.datasource.local

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.common.Fakes
import com.florent37.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import com.florent37.flocondesktop.features.network.data.FloconHttpRequestGenerator
import com.florent37.flocondesktop.features.network.data.datasource.local.mapper.toDomainModel
import com.florent37.flocondesktop.features.network.data.datasource.local.mapper.toEntity
import com.florent37.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
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
                val fakeRequests = FloconHttpRequestGenerator.generateDynamicFakeFloconHttpRequests(count = 10)
                fakeRequests.forEach { domainModel ->
                    floconHttpRequestDao.upsertRequest(domainModel.toEntity(Fakes.FakeDeviceId))
                }
            }
        }
    }

    override fun observeRequests(deviceId: DeviceId): Flow<List<FloconHttpRequestDomainModel>> = floconHttpRequestDao
        .observeRequests(deviceId)
        .map { entities ->
            entities.map { it.toDomainModel() }
        }.flowOn(dispatcherProvider.data)

    override suspend fun save(
        deviceId: DeviceId,
        request: FloconHttpRequestDomainModel,
    ) {
        withContext(dispatcherProvider.data) {
            val entity = request.toEntity(deviceId)
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

    override suspend fun clearDeviceCalls(deviceId: DeviceId) {
        withContext(dispatcherProvider.data) {
            floconHttpRequestDao.clearDeviceCalls(deviceId = deviceId)
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
