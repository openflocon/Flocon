package io.github.openflocon.data.local.database.dao

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import io.github.openflocon.data.local.database.models.DabataseTableEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface TablesDao {
    @Upsert
    suspend fun insertTable(table: DabataseTableEntity)

    @Query("""
        SELECT * FROM DabataseTableEntity 
        WHERE deviceId = :deviceId 
        AND packageName = :packageName
        AND databaseId = :databaseId
    """)
    suspend fun observe(
        deviceId: String,
        packageName: String,
        databaseId: String,
    ) : Flow<List<DabataseTableEntity>>

    @Query("""
        DELETE FROM DabataseTableEntity 
        WHERE deviceId = :deviceId 
        AND packageName = :packageName
        AND databaseId = :databaseId
        AND tableName NOT IN (:tablesNames)
    """)
    suspend fun removeTablesNotPresentAnymore(
        deviceId: String,
        packageName: String,
        databaseId: String,
        tablesNames: List<String>
    )
}
