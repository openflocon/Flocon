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
    ) {
        databaseQueryLogDao.insert(
            DatabaseQueryLogEntity(
                dbName = log.dbName,
                sqlQuery = log.sqlQuery,
                bindArgs = log.bindArgs?.let { json.encodeToString(it) },
                timestamp = log.timestamp,
                isTransaction = log.isTransaction,
            )
        )
    }

    override fun observeQueryLogs(dbName: String, showTransactions: Boolean, filterChips: List<String>): Flow<PagingData<DatabaseQueryLogDomainModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
            ),
            pagingSourceFactory = {
                val query = buildQuery(dbName, showTransactions, filterChips)

                databaseQueryLogDao.getPagingSource(
                    query = query
                )
            }
        ).flow
            .map { pagingData ->
                pagingData.map { entity ->
                    DatabaseQueryLogDomainModel(
                        dbName = entity.dbName,
                        sqlQuery = entity.sqlQuery,
                        bindArgs = entity.bindArgs?.let {
                            json.decodeFromString(it)
                        } ?: emptyList(),
                        timestamp = entity.timestamp,
                        isTransaction = entity.isTransaction,
                    )
                }
            }
    }

    private fun buildQuery(
        dbName: String,
        showTransactions: Boolean,
        filterChips: List<String>
    ): RoomRawQuery {
        val queryParams = ArrayList<Any>()
        var queryString = "SELECT * FROM DatabaseQueryLogEntity WHERE dbName = ?"
        queryParams.add(dbName)

        if (!showTransactions) {
            queryString += " AND isTransaction = 0"
        }

        if (filterChips.isNotEmpty()) {
            queryString += " AND ("
            filterChips.forEachIndexed { index, chip ->
                if (index > 0) queryString += " OR "
                queryString += "(sqlQuery LIKE ? OR bindArgs LIKE ?)"
                queryParams.add("%${chip}%")
                queryParams.add("%${chip}%")
            }
            queryString += ")"
        }

        queryString += " ORDER BY timestamp DESC"

        val query = RoomRawQuery(
            sql = queryString,
            onBindStatement = { statement ->
                queryParams.forEachIndexed { index, arg ->
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
        return query
    }
}
