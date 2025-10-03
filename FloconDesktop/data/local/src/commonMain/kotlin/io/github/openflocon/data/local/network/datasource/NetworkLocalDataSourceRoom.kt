package io.github.openflocon.data.local.network.datasource

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.filter
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
import androidx.paging.PagingState

class NetworkLocalDataSourceRoom(
    private val floconNetworkDao: FloconNetworkDao,
) : NetworkLocalDataSource {

    override fun observeRequests(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sortedBy: NetworkSortDomainModel?,
        filter: NetworkFilterDomainModel,
    ): Flow<PagingData<FloconNetworkCallDomainModel>> {
        val pagingSourceFactory = {
            RawQueryPagingSource(
                floconNetworkDao = floconNetworkDao,
                createQuery = { limit, offset -> // Le lambda qui construit la requête
                    observeRequestsRaw(
                        deviceIdAndPackageName = deviceIdAndPackageName,
                        sortedBy = sortedBy,
                        filter = filter,
                        limit = limit,
                        offset = offset
                    )
                }
            )
        }

        return Pager(
            config = PagingConfig(
                pageSize = 5,
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
            .map { pagingData ->
                pagingData.map { entity -> entity.toDomainModel()!! } // TODO
            }
    }

    private fun observeRequestsRaw(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sortedBy: NetworkSortDomainModel?,
        filter: NetworkFilterDomainModel,
        limit: Int, // <-- Ajout de LIMIT
        offset: Int, // <-- Ajout de OFFSET
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

            // Ajout de la pagination
            appendLine("LIMIT ?") // Ajout de LIMIT
            appendLine("OFFSET ?") // Ajout de OFFSET
        }.trimIndent()

        args.add(limit)
        args.add(offset)

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

class RawQueryPagingSource(
    private val floconNetworkDao: FloconNetworkDao,
    private val createQuery: (limit: Int, offset: Int) -> RoomRawQuery,
) : PagingSource<Int, FloconNetworkCallEntity>() {

    // Définition de la clé initiale (position dans la liste)
    override fun getRefreshKey(state: PagingState<Int, FloconNetworkCallEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(state.config.pageSize)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(state.config.pageSize)
        }
    }

    // Logique de chargement des pages
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FloconNetworkCallEntity> {
        return try {
            // Clé de page : index de début (offset)
            val offset = params.key ?: 0 // Si key est null, on commence au début

            // Nombre d'éléments à charger (limit)
            val limit = params.loadSize

            // 1. Générer la requête SQL avec LIMIT et OFFSET
            val rawQueryWithPagination = createQuery(limit, offset)

            // 2. Exécuter la requête via un DAO @RawQuery.
            // ATTENTION : Cette méthode DAO doit être modifiée pour ne pas retourner un Flow<List<...>>
            // mais une List<...> simple, et devrait être suspendue pour être appelée ici.
            // Nous allons supposer que vous avez une fonction DAO simple :
            // @RawQuery(observedEntities = [FloconNetworkCallEntity::class])
            // suspend fun getRequestsRaw(query: SupportSQLiteQuery): List<FloconNetworkCallEntity>
            val entities = floconNetworkDao.observeRequestsRaw(rawQueryWithPagination)

            // Calcul de la clé de la page suivante
            val nextKey = if (entities.isEmpty()) {
                null
            } else {
                offset + entities.size
            }

            // Retourner le résultat
            LoadResult.Page(
                data = entities,
                prevKey = if (offset == 0) null else offset - params.loadSize, // Key de la page précédente
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
