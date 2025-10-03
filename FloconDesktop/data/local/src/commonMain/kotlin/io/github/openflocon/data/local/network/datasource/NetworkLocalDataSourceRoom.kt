package io.github.openflocon.data.local.network.datasource

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.RoomRawQuery
import io.github.openflocon.data.core.network.datasource.NetworkLocalDataSource
import io.github.openflocon.data.local.network.dao.FloconNetworkDao
import io.github.openflocon.data.local.network.mapper.toDomainModel
import io.github.openflocon.data.local.network.mapper.toEntity
import io.github.openflocon.data.local.network.models.FloconNetworkCallEntity
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.NetworkFilterDomainModel
import io.github.openflocon.domain.network.models.NetworkSortDomainModel
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NetworkLocalDataSourceRoom(
    private val floconNetworkDao: FloconNetworkDao,
) : NetworkLocalDataSource {

    override suspend fun getRequests(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sortedBy: NetworkSortDomainModel?,
        filter: NetworkFilterDomainModel,
    ): List<FloconNetworkCallDomainModel> = floconNetworkDao.getRequestsRaw(
        generateNetworkRequestsRawQuery(
            deviceIdAndPackageName = deviceIdAndPackageName,
            sortedBy = sortedBy,
            filter = filter,
        )
    ).mapNotNull(FloconNetworkCallEntity::toDomainModel)


    override fun observeRequests(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sortedBy: NetworkSortDomainModel?,
        filter: NetworkFilterDomainModel,
    ): Flow<PagingData<FloconNetworkCallDomainModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
            ),
            pagingSourceFactory = {
                floconNetworkDao.observeRequestsWithPaging(
                    generateNetworkRequestsRawQuery(
                        deviceIdAndPackageName = deviceIdAndPackageName,
                        sortedBy = sortedBy,
                        filter = filter,
                    )
                )
            }
        ).flow
            .map { pagingData ->
                pagingData.map { entity -> entity.toDomainModel()!! } // TODO
            }
    }

    private fun generateNetworkRequestsRawQuery(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sortedBy: NetworkSortDomainModel?,
        filter: NetworkFilterDomainModel,
    ): RoomRawQuery {
        val safeColumnName = sortedBy?.column?.roomColumnName() ?: "request_startTime"

        val sortOrder = if (sortedBy == null || sortedBy.asc) "ASC" else "DESC"

        val args = mutableListOf<Any>()

        val sqlQuery = buildString {
            appendLine("SELECT * FROM FloconNetworkCallEntity")
            appendLine("WHERE deviceId = ?")
            args.add(deviceIdAndPackageName.deviceId)

            appendLine("AND packageName = ?")
            args.add(deviceIdAndPackageName.packageName)

            filter.filterOnAllColumns?.let { filterText ->
                appendLine("AND (")
                val columns = listOf(
                    "request_startTimeFormatted",
                    "request_domainFormatted",
                    "request_methodFormatted",
                    "request_queryFormatted",
                    "request_url",
                    "request_requestBody",
                    "response_responseBody",
                    "response_durationFormatted",
                )
                columns.forEachIndexed { index, column ->
                    appendLine("$column LIKE ? COLLATE NOCASE")
                    args.add("%$filterText%")
                    if (index != columns.lastIndex) {
                        appendLine("OR")
                    }
                }
                appendLine(")")
            }

            filter.textsFilters?.forEach { filter ->
                filter.includedFilters.takeIf { it.isNotEmpty() }?.let { included ->
                    appendLine("AND (")
                    included.forEachIndexed { includedFilterIndex, filterItem ->
                        appendLine("\t${filter.column.roomColumnName()} LIKE ? COLLATE NOCASE")
                        args.add("%${filterItem.text}%")

                        if (includedFilterIndex != included.lastIndex) {
                            appendLine("\tOR")
                        }
                    }
                    appendLine(")")
                }
                filter.excludedFilters.takeIf { it.isNotEmpty() }?.let { excluded ->
                    appendLine("AND (")
                    excluded.forEachIndexed { excludedFilterIndex, filterItem ->
                        appendLine("\t${filter.column.roomColumnName()} NOT LIKE ? COLLATE NOCASE")
                        args.add("%${filterItem.text}%")

                        if (excludedFilterIndex != excluded.lastIndex) {
                            appendLine("\tAND")
                        }
                    }
                    appendLine(")")
                }
            }

            filter.methodFilter?.takeIf { it.isNotEmpty() }?.let { methodFilter ->
                appendLine("AND (")
                methodFilter.forEachIndexed { includedFilterIndex, filterItem ->
                    appendLine("\trequest_methodFormatted LIKE ? COLLATE NOCASE")
                    args.add("%${filterItem}%")

                    if (includedFilterIndex != methodFilter.lastIndex) {
                        appendLine("\tOR")
                    }
                }
                appendLine(")")
            }

            appendLine("ORDER BY $safeColumnName $sortOrder")
        }.trimIndent()

        val roomQuery = RoomRawQuery(sqlQuery, onBindStatement = {
            args.forEachIndexed { index, item ->
                when (item) {
                    is String -> {
                        it.bindText(index + 1, item)
                    }

                    is Int -> {
                        it.bindInt(index + 1, item)
                    }

                    is Long -> {
                        it.bindLong(index + 1, item)
                    }
                }
            }
        })

        return roomQuery
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
    ): FloconNetworkCallDomainModel? {
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

// TODO maybe merge with NetworkSortDomainModel.Column
private fun NetworkTextFilterColumns.roomColumnName(): String = when (this) {
    NetworkTextFilterColumns.RequestTime -> "request_startTimeFormatted"
    NetworkTextFilterColumns.Domain -> "request_domainFormatted"
    NetworkTextFilterColumns.Query -> "request_queryFormatted"
    NetworkTextFilterColumns.Status -> "response_statusFormatted"
    NetworkTextFilterColumns.Time -> "response_durationMs"
}

private fun NetworkSortDomainModel.Column.roomColumnName(): String = when (this) {
    NetworkSortDomainModel.Column.RequestStartTimeFormatted -> "request_startTimeFormatted"
    NetworkSortDomainModel.Column.Method -> "request_methodFormatted"
    NetworkSortDomainModel.Column.Domain -> "request_domainFormatted"
    NetworkSortDomainModel.Column.Query -> "request_queryFormatted"
    NetworkSortDomainModel.Column.Status -> "response_statusFormatted"
    NetworkSortDomainModel.Column.Duration -> "response_durationMs"
}
