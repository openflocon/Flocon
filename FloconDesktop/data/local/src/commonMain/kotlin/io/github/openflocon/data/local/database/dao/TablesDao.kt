package io.github.openflocon.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.github.openflocon.data.local.database.models.DatabaseTableEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TablesDao {
    @Upsert
    suspend fun insertTable(table: DatabaseTableEntity)

    @Query(
        """
        SELECT * FROM DatabaseTableEntity 
        WHERE deviceId = :deviceId 
        AND packageName = :packageName
        AND databaseId = :databaseId
    """
    )
    fun observe(
        deviceId: String,
        packageName: String,
        databaseId: String,
    ): Flow<List<DatabaseTableEntity>>

    @Query(
        """
        DELETE FROM DatabaseTableEntity 
        WHERE deviceId = :deviceId 
        AND packageName = :packageName
        AND databaseId = :databaseId
        AND tableName NOT IN (:tablesNames)
    """
    )
    suspend fun removeTablesNotPresentAnymore(
        deviceId: String,
        packageName: String,
        databaseId: String,
        tablesNames: List<String>
    )
}
