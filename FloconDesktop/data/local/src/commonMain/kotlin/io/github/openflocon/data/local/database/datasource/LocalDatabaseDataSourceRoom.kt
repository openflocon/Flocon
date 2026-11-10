package io.github.openflocon.data.local.database.datasource

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.RoomRawQuery
import io.github.openflocon.data.core.database.datasource.LocalDatabaseDataSource
import io.github.openflocon.data.local.database.dao.DatabaseQueryLogDao
import io.github.openflocon.data.local.database.dao.QueryDao
import io.github.openflocon.data.local.database.dao.TablesDao
import io.github.openflocon.data.local.database.mapper.toDomain
import io.github.openflocon.data.local.database.mapper.toEntity
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

// the logs of a transaction form one entry, a query outside of a transaction is an entry by itself
private const val ENTRY_KEY = "COALESCE(transactionId, 'q' || id)"

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
        val nestedPath = nestedPathOf(log, deviceIdAndPackageName)
        databaseQueryLogDao.insert(
            DatabaseQueryLogEntity(
                dbId = log.dbId,
                dbName = log.dbName,
                sqlQuery = log.sqlQuery,
                bindArgs = log.bindArgs?.let { json.encodeToString(it) },
                timestamp = log.timestamp,
                isTransaction = log.isTransaction,
                transactionId = log.transactionId,
                nestedTransactionId = log.nestedTransactionId,
                parentTransactionId = log.parentTransactionId,
                transactionDepth = log.transactionDepth,
                nestedPath = nestedPath,
                deviceId = deviceIdAndPackageName.deviceId,
                packageName = deviceIdAndPackageName.packageName,
                appInstance = log.appInstance
            )
        )
    }

    // the path of a nested transaction is the one of its parent and its own id. The first query of a nested transaction (its savepoint)
    // is logged after its parent has logged some, so the parent is known
    private suspend fun nestedPathOf(
        log: DatabaseQueryLogDomainModel,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): String? {
        val transactionId = log.transactionId ?: return null
        val nestedId = log.nestedTransactionId ?: return null
        val parentId = log.parentTransactionId
        val parentPath = if (parentId == null || parentId == transactionId) {
            null
        } else {
            databaseQueryLogDao.findNestedPath(
                deviceId = deviceIdAndPackageName.deviceId,
                packageName = deviceIdAndPackageName.packageName,
                dbId = log.dbId,
                transactionId = transactionId,
                nestedTransactionId = parentId,
            )
        }
        return if (parentPath.isNullOrEmpty()) nestedId else "$parentPath,$nestedId"
    }

    override fun observeQueryLogs(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        dbId: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>,
        limit: Int,
        offset: Int,
    ): Flow<List<DatabaseQueryLogDomainModel>> {
        val query = buildQuery(
            dbId = dbId,
            showTransactions = showTransactions,
            filters = filters,
            deviceIdAndPackageName = deviceIdAndPackageName,
            limit = limit,
            offset = offset,
        )

        return databaseQueryLogDao.observeLogs(query).map { list ->
            list.map { entity -> entity.toDomain() }
        }
    }

    override fun countQueryLogs(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        dbId: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>
    ): Flow<Int> {
        val query = buildCountQuery(
            dbId = dbId,
            showTransactions = showTransactions,
            filters = filters,
            deviceIdAndPackageName = deviceIdAndPackageName
        )
        return databaseQueryLogDao.countLogs(query)
    }

    override suspend fun getQueryLogs(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        dbId: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>
    ): List<DatabaseQueryLogDomainModel> {
        val query = buildQuery(
            dbId = dbId,
            showTransactions = showTransactions,
            filters = filters,
            deviceIdAndPackageName = deviceIdAndPackageName,
            limit = null,
            offset = null,
        )
        return databaseQueryLogDao.getLogs(query).map { entity -> entity.toDomain() }
    }

    override suspend fun getLatestQueryLogs(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        dbId: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>,
        appInstance: Long?,
        transactionId: String?,
        limit: Int,
    ): List<DatabaseQueryLogDomainModel> {
        val (whereClause, queryParams) = buildWhereClauseAndParams(
            dbId = dbId,
            showTransactions = showTransactions,
            filters = filters,
            deviceIdAndPackageName = deviceIdAndPackageName,
            appInstance = appInstance,
            transactionId = transactionId,
        )
        queryParams.add(limit)
        val query = createRawQuery("SELECT * FROM DatabaseQueryLogEntity $whereClause ORDER BY timestamp DESC LIMIT ?", queryParams)

        return databaseQueryLogDao.getLogs(query).map { entity -> entity.toDomain() }
    }

    override fun observeQueryLog(id: Int): Flow<DatabaseQueryLogDomainModel?> = databaseQueryLogDao.observeLog(id).map { entity ->
        entity?.toDomain()
    }

    private fun DatabaseQueryLogEntity.toDomain() = DatabaseQueryLogDomainModel(
        id = id,
        dbId = dbId,
        dbName = dbName,
        sqlQuery = sqlQuery,
        bindArgs = bindArgs?.let { json.decodeFromString(it) } ?: emptyList(),
        timestamp = timestamp,
        isTransaction = isTransaction,
        transactionId = transactionId,
        nestedTransactionId = nestedTransactionId,
        parentTransactionId = parentTransactionId,
        transactionDepth = transactionDepth,
        appInstance = appInstance,
    )

    override suspend fun deleteAllQueryLogs(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        databaseQueryLogDao.deleteAll(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
        )
    }

    private fun buildQuery(
        dbId: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        limit: Int?,
        offset: Int?,
    ): RoomRawQuery {
        val (whereClause, queryParams) = buildWhereClauseAndParams(
            dbId = dbId,
            showTransactions = showTransactions,
            filters = filters,
            deviceIdAndPackageName = deviceIdAndPackageName,
            // the full list (exports) is flat, only the pages of the screen are made of groups
            keepGroupedControls = limit != null,
        )

        if (limit == null || offset == null) {
            return createRawQuery("SELECT * FROM DatabaseQueryLogEntity $whereClause ORDER BY timestamp ASC, id ASC", queryParams)
        }

        // the pages hold entries: all the logs of a transaction are in the same page, as a standalone query is one entry
        val pageParams = ArrayList<Any>(queryParams).apply {
            addAll(queryParams)
            add(limit)
            add(offset)
        }
        val queryString = "SELECT * FROM DatabaseQueryLogEntity $whereClause AND $ENTRY_KEY IN (" +
            "SELECT $ENTRY_KEY FROM DatabaseQueryLogEntity $whereClause " +
            "GROUP BY $ENTRY_KEY ORDER BY MIN(timestamp) ASC, MIN(id) ASC LIMIT ? OFFSET ?) " +
            "ORDER BY timestamp ASC, id ASC"
        return createRawQuery(queryString, pageParams)
    }

    private fun buildCountQuery(
        dbId: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): RoomRawQuery {
        val (whereClause, queryParams) = buildWhereClauseAndParams(
            dbId = dbId,
            showTransactions = showTransactions,
            filters = filters,
            deviceIdAndPackageName = deviceIdAndPackageName,
            keepGroupedControls = true,
        )

        val queryString = "SELECT COUNT(DISTINCT $ENTRY_KEY) FROM DatabaseQueryLogEntity $whereClause"
        return createRawQuery(queryString, queryParams)
    }

    private fun buildWhereClauseAndParams(
        dbId: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        appInstance: Long? = null,
        transactionId: String? = null,
        keepGroupedControls: Boolean = false,
    ): Pair<String, ArrayList<Any>> {
        val queryParams = ArrayList<Any>()
        var whereClause = "WHERE deviceId = ? AND packageName = ? AND dbId = ?"
        queryParams.add(deviceIdAndPackageName.deviceId)
        queryParams.add(deviceIdAndPackageName.packageName)
        queryParams.add(dbId)

        if (appInstance != null) {
            whereClause += " AND appInstance = ?"
            queryParams.add(appInstance)
        }

        if (transactionId != null) {
            whereClause += " AND transactionId = ?"
            queryParams.add(transactionId)
        }

        if (!showTransactions) {
            whereClause += if (keepGroupedControls) {
                // the BEGIN / COMMIT of a transaction with an id are kept: the screen needs them for the status of the group
                " AND (isTransaction = 0 OR transactionId IS NOT NULL)"
            } else {
                " AND isTransaction = 0"
            }
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

    private fun createRawQuery(sql: String, params: List<Any>): RoomRawQuery = RoomRawQuery(
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
