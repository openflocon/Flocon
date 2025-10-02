package io.github.openflocon.data.local.network.datasource

import androidx.room.RoomRawQuery
import io.github.openflocon.data.core.network.datasource.NetworkLocalDataSource
import io.github.openflocon.data.local.network.dao.FloconNetworkDao
import io.github.openflocon.data.local.network.mapper.toDomainModel
import io.github.openflocon.data.local.network.mapper.toEntity
import io.github.openflocon.data.local.network.models.FloconNetworkCallEntity
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.NetworkSortedBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NetworkLocalDataSourceRoom(
    private val floconNetworkDao: FloconNetworkDao,
) : NetworkLocalDataSource {

    override fun observeRequests(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sortedBy: NetworkSortedBy?,
    ): Flow<List<FloconNetworkCallDomainModel>> = floconNetworkDao.let {
        observeRequestsRaw(
            deviceIdAndPackageName = deviceIdAndPackageName,
            sortedBy = NetworkSortedBy(
                column = NetworkSortedBy.Column.RequestStartTimeFormatted,
                asc = true,
            )
        ).map { entities -> entities.mapNotNull(FloconNetworkCallEntity::toDomainModel) }
    }

    private fun observeRequestsRaw(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sortedBy: NetworkSortedBy?,
    ): Flow<List<FloconNetworkCallEntity>> {
        val safeColumnName = sortedBy?.column?.roomColumnName() ?: "request_startTime"

        val sortOrder = if (sortedBy == null || sortedBy.asc) "ASC" else "DESC"

        val sqlQuery = """
            SELECT * FROM FloconNetworkCallEntity 
            WHERE deviceId = ? 
            AND packageName = ? 
            ORDER BY $safeColumnName $sortOrder
        """.trimIndent()

        val roomQuery = RoomRawQuery(sqlQuery, onBindStatement = {
            it.bindText(1, deviceIdAndPackageName.deviceId)
            it.bindText(2, deviceIdAndPackageName.packageName)
        })

        return floconNetworkDao.observeRequestsRaw(roomQuery)
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


fun NetworkSortedBy.Column.roomColumnName(): String = when (this) {
    NetworkSortedBy.Column.RequestStartTimeFormatted -> "request_startTimeFormatted"
    NetworkSortedBy.Column.Method -> "request_method"
    NetworkSortedBy.Column.Domain -> "request_domain" // todo parse before
    NetworkSortedBy.Column.Query -> "request_query" // TODO parse before
    NetworkSortedBy.Column.Status -> "status" // todo parse before,
    NetworkSortedBy.Column.DurationFormatted -> "response_durationFormatted"
}
