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
}
