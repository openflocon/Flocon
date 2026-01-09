package io.github.openflocon.data.local.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.RoomRawQuery
import io.github.openflocon.data.local.database.models.DatabaseQueryLogEntity

@Dao
interface DatabaseQueryLogDao {
    @Insert
    suspend fun insert(entity: DatabaseQueryLogEntity)

    @androidx.room.RawQuery(observedEntities = [DatabaseQueryLogEntity::class])
    fun getPagingSource(
        query: RoomRawQuery,
    ): PagingSource<Int, DatabaseQueryLogEntity>

    @androidx.room.RawQuery(observedEntities = [DatabaseQueryLogEntity::class])
    fun observeLogs(
        query: RoomRawQuery,
    ): kotlinx.coroutines.flow.Flow<List<DatabaseQueryLogEntity>>

    @androidx.room.RawQuery(observedEntities = [DatabaseQueryLogEntity::class])
    fun countLogs(
        query: RoomRawQuery,
    ): kotlinx.coroutines.flow.Flow<Int>

    @androidx.room.RawQuery(observedEntities = [DatabaseQueryLogEntity::class])
    suspend fun getLogs(
        query: RoomRawQuery,
    ): List<DatabaseQueryLogEntity>

    @androidx.room.Query("DELETE FROM DatabaseQueryLogEntity WHERE deviceId = :deviceId AND packageName = :packageName")
    suspend fun deleteAll(deviceId: String, packageName: String)
}
