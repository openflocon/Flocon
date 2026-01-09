package io.github.openflocon.data.local.database.datasource

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.github.openflocon.data.core.database.datasource.LocalDatabaseDataSource
import io.github.openflocon.data.local.database.dao.DatabaseQueryLogDao
import io.github.openflocon.data.local.database.dao.QueryDao
import io.github.openflocon.data.local.database.dao.TablesDao
import io.github.openflocon.data.local.database.mapper.toDomain
import io.github.openflocon.data.local.database.mapper.toEntity
import androidx.paging.map
import androidx.room.RoomRawQuery
import io.github.openflocon.data.local.database.models.DatabaseQueryLogEntity
import io.github.openflocon.data.local.database.models.FavoriteQueryEntity
import io.github.openflocon.data.local.database.models.SuccessQueryEntity
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.database.models.DatabaseFavoriteQueryDomainModel
import io.github.openflocon.domain.database.models.DatabaseQueryLogDomainModel
import io.github.openflocon.domain.database.models.DatabaseTableDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseId
import io.github.openflocon.domain.database.models.FilterQueryLogDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlin.let

internal class LocalDatabaseDataSourceRoom(
    private val queryDao: QueryDao,
    private val tablesDao: TablesDao,
    private val databaseQueryLogDao: DatabaseQueryLogDao,
    private val json: Json,
) : LocalDatabaseDataSource {

    override suspend fun saveSuccessQuery(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
        query: String
    ) {
        if (queryDao.doesQueryExists(
                deviceId = deviceIdAndPackageName.deviceId,
                packageName = deviceIdAndPackageName.packageName,
                databaseId = databaseId,
                queryString = query,
            )
        ) {
            // put it in top if we already have it
            queryDao.deleteQuery(
                deviceId = deviceIdAndPackageName.deviceId,
                packageName = deviceIdAndPackageName.packageName,
                databaseId = databaseId,
                queryString = query,
            )
        }

        val entity = SuccessQueryEntity(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            databaseId = databaseId,
            queryString = query,
            timestamp = System.currentTimeMillis(),
        )
        queryDao.insertSuccessQuery(entity)
    }

    override fun observeLastSuccessQuery(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId
    ): Flow<List<String>> = queryDao.observeSuccessQueriesByDeviceId(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
        databaseId = databaseId,
    )

    override suspend fun saveTable(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
        table: DatabaseTableDomainModel
    ) {
        tablesDao.insertTable(
            table = table.toEntity(
                databaseId = databaseId,
                deviceIdAndPackageName = deviceIdAndPackageName,
                json = json,
            )
        )
    }

    override suspend fun observe(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
    ): Flow<List<DatabaseTableDomainModel>> = tablesDao.observe(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
        databaseId = databaseId,
    ).map {
        it.map {
            it.toDomain(
                json = json,
            )
        }
    }

    override suspend fun removeTablesNotPresentAnymore(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
        tablesNames: List<String>
    ) {
        tablesDao.removeTablesNotPresentAnymore(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            databaseId = databaseId,
            tablesNames = tablesNames,
        )
    }

    override suspend fun saveAsFavorite(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: String,
        title: String,
        query: String
    ): Either<Throwable, Unit> {
        val existing = queryDao.getFavoriteQueryByTitle(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            databaseId = databaseId,
            title = title,
        )
        val timestamp = System.currentTimeMillis()
        val title = if (existing != null) {
            "${existing.title} ($timestamp)"
        } else {
            title
        }
        queryDao.insertFavoriteQuery(
            FavoriteQueryEntity(
                deviceId = deviceIdAndPackageName.deviceId,
                packageName = deviceIdAndPackageName.packageName,
                databaseId = databaseId,
                queryString = query,
                title = title,
                timestamp = System.currentTimeMillis(),
            )
        )

        return Success(Unit)
    }

    override suspend fun deleteFavorite(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: String,
        id: Long
    ): Either<Throwable, Unit> {
        queryDao.deleteFavoriteQuery(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            databaseId = databaseId,
            favoriteId = id,
        )
        return Success(Unit)
    }

    override fun observeFavorites(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ): Flow<List<DatabaseFavoriteQueryDomainModel>> = queryDao.observeFavoriteQueryEntity(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
    ).map { list ->
        list.map {
            it.toDomain()
        }
    }.distinctUntilChanged()

    override suspend fun getFavorite(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: String,
        id: Long
    ): DatabaseFavoriteQueryDomainModel? = queryDao.getFavorite(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
        id = id,
    )?.toDomain()

    override suspend fun saveQueryLog(
        log: DatabaseQueryLogDomainModel,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ) {
        databaseQueryLogDao.insert(
            DatabaseQueryLogEntity(
                dbName = log.dbName,
                sqlQuery = log.sqlQuery,
                bindArgs = log.bindArgs?.let { json.encodeToString(it) },
                timestamp = log.timestamp,
                isTransaction = log.isTransaction,
                deviceId = deviceIdAndPackageName.deviceId,
                packageName = deviceIdAndPackageName.packageName,
                appInstance = log.appInstance
            )
        )
    }

    override fun observeQueryLogs(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        dbName: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>,
        limit: Int,
        offset: Int,
    ): Flow<List<DatabaseQueryLogDomainModel>> {
        val query = buildQuery(
            dbName = dbName,
            showTransactions = showTransactions,
            filters = filters,
            deviceIdAndPackageName = deviceIdAndPackageName,
            limit = limit,
            offset = offset,
        )

        return databaseQueryLogDao.observeLogs(query).map { list ->
            list.map { entity ->
                DatabaseQueryLogDomainModel(
                    dbName = entity.dbName,
                    sqlQuery = entity.sqlQuery,
                    bindArgs = entity.bindArgs?.let {
                        json.decodeFromString(it)
                    } ?: emptyList(),
                    timestamp = entity.timestamp,
                    isTransaction = entity.isTransaction,
                    appInstance = entity.appInstance
                )
            }
        }
    }

    override fun countQueryLogs(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        dbName: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>
    ): Flow<Int> {
        val query = buildCountQuery(
            dbName = dbName,
            showTransactions = showTransactions,
            filters = filters,
            deviceIdAndPackageName = deviceIdAndPackageName
        )
        return databaseQueryLogDao.countLogs(query)
    }

    override suspend fun getQueryLogs(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        dbName: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>
    ): List<DatabaseQueryLogDomainModel> {
        val query = buildQuery(
            dbName = dbName,
            showTransactions = showTransactions,
            filters = filters,
            deviceIdAndPackageName = deviceIdAndPackageName,
            limit = null,
            offset = null,
        )
        return databaseQueryLogDao.getLogs(query).map { entity ->
            DatabaseQueryLogDomainModel(
                dbName = entity.dbName,
                sqlQuery = entity.sqlQuery,
                bindArgs = entity.bindArgs?.let {
                    json.decodeFromString(it)
                } ?: emptyList(),
                timestamp = entity.timestamp,
                isTransaction = entity.isTransaction,
                appInstance = entity.appInstance
            )
        }
    }

    override suspend fun deleteAllQueryLogs(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        databaseQueryLogDao.deleteAll(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
        )
    }

    private fun buildQuery(
        dbName: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        limit: Int?,
        offset: Int?,
    ): RoomRawQuery {
        val (whereClause, queryParams) = buildWhereClauseAndParams(
            dbName = dbName,
            showTransactions = showTransactions,
            filters = filters,
            deviceIdAndPackageName = deviceIdAndPackageName,
        )

        var queryString = "SELECT * FROM DatabaseQueryLogEntity $whereClause"
        queryString += " ORDER BY timestamp ASC"

        if (limit != null && offset != null) {
            queryString += " LIMIT ? OFFSET ?"
            queryParams.add(limit)
            queryParams.add(offset)
        }

        return createRawQuery(queryString, queryParams)
    }

    private fun buildCountQuery(
        dbName: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): RoomRawQuery {
        val (whereClause, queryParams) = buildWhereClauseAndParams(
            dbName = dbName,
            showTransactions = showTransactions,
            filters = filters,
            deviceIdAndPackageName = deviceIdAndPackageName,
        )

        val queryString = "SELECT COUNT(*) FROM DatabaseQueryLogEntity $whereClause"
        return createRawQuery(queryString, queryParams)
    }

    private fun buildWhereClauseAndParams(
        dbName: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Pair<String, ArrayList<Any>> {
        val queryParams = ArrayList<Any>()
        var whereClause = "WHERE deviceId = ? AND packageName = ? AND dbName = ?"
        queryParams.add(deviceIdAndPackageName.deviceId)
        queryParams.add(deviceIdAndPackageName.packageName)
        queryParams.add(dbName)

        if (!showTransactions) {
            whereClause += " AND isTransaction = 0"
        }

        val includes = filters.filter { it.type == FilterQueryLogDomainModel.FilterType.INCLUDE }
        val excludes = filters.filter { it.type == FilterQueryLogDomainModel.FilterType.EXCLUDE }

        if (includes.isNotEmpty()) {
            whereClause += " AND ("
            includes.forEachIndexed { index, filter ->
                if (index > 0) whereClause += " OR "
                whereClause += "(sqlQuery LIKE ? OR bindArgs LIKE ?)"
                queryParams.add("%${filter.text}%")
                queryParams.add("%${filter.text}%")
            }
            whereClause += ")"
        }

        if (excludes.isNotEmpty()) {
            excludes.forEach { filter ->
                whereClause += " AND NOT (sqlQuery LIKE ? OR bindArgs LIKE ?)"
                queryParams.add("%${filter.text}%")
                queryParams.add("%${filter.text}%")
            }
        }
        return whereClause to queryParams
    }

    private fun createRawQuery(sql: String, params: List<Any>): RoomRawQuery {
        return RoomRawQuery(
            sql = sql,
            onBindStatement = { statement ->
                params.forEachIndexed { index, arg ->
                    when (arg) {
                        is String -> statement.bindText(index + 1, arg)
                        is Long -> statement.bindLong(index + 1, arg)
                        is Int -> statement.bindLong(index + 1, arg.toLong())
                        is Boolean -> statement.bindLong(index + 1, if (arg) 1L else 0L)
                        is Double -> statement.bindDouble(index + 1, arg)
                        is Float -> statement.bindDouble(index + 1, arg.toDouble())
                        else -> statement.bindText(index + 1, arg.toString())
                    }
                }
            }
        )
    }
}
