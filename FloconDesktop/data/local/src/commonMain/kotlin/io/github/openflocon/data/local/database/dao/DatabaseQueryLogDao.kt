package io.github.openflocon.data.local.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.github.openflocon.data.local.database.models.DatabaseQueryLogEntity

@Dao
interface DatabaseQueryLogDao {
    @Insert
    suspend fun insert(entity: DatabaseQueryLogEntity)

    @Query("""
        SELECT * FROM DatabaseQueryLogEntity 
        WHERE dbName = :dbName 
        AND (:showTransactions = 1 OR isTransaction = 0) 
        ORDER BY timestamp DESC
    """)
    fun getPagingSource(dbName: String, showTransactions: Boolean): PagingSource<Int, DatabaseQueryLogEntity>
}
