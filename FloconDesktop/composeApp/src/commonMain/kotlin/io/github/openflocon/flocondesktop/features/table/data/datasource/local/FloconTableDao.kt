package io.github.openflocon.flocondesktop.features.table.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.table.data.datasource.local.model.TableEntity
import io.github.openflocon.flocondesktop.features.table.data.datasource.local.model.TableItemEntity
import io.github.openflocon.flocondesktop.features.table.domain.model.TableId
import kotlinx.coroutines.flow.Flow

@Dao
interface FloconTableDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTable(table: TableEntity): Long

    @Query(
        """
        SELECT id
        FROM TableEntity 
        WHERE deviceId = :deviceId AND name = :tableName LIMIT 1
    """,
    )
    suspend fun getTableId(deviceId: DeviceId, tableName: String): Long?

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTableItems(tableItemEntities: List<TableItemEntity>)

    @Query(
        """
        SELECT * 
        FROM TableEntity 
        WHERE deviceId = :deviceId AND id = :tableId LIMIT 1
    """,
    )
    fun observeTable(deviceId: DeviceId, tableId: TableId): Flow<TableEntity?>

    @Query(
        """
        SELECT * 
        FROM TableEntity 
        WHERE deviceId = :deviceId
    """,
    )
    fun observeTablesForDevice(deviceId: DeviceId): Flow<List<TableEntity>>

    @Query(
        """
        SELECT * 
        FROM TableEntity 
        WHERE deviceId = :deviceId
    """,
    )
    suspend fun getTablesForDevice(deviceId: DeviceId): List<TableEntity>

    @Query(
        """
        SELECT * 
        FROM TableItemEntity 
        WHERE tableId = :tableId 
        ORDER BY createdAt ASC
    """,
    )
    fun observeTableItems(tableId: Long): Flow<List<TableItemEntity>>

    @Query(
        """
        DELETE FROM TableItemEntity
        WHERE tableId = :tableId
        """,
    )
    suspend fun deleteTableContent(tableId: Long)
}
