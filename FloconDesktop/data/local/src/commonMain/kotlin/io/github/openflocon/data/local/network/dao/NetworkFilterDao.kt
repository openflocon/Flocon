package io.github.openflocon.data.local.network.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.openflocon.data.local.network.models.NetworkFilterEntity
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import kotlinx.coroutines.flow.Flow

@Dao
interface NetworkFilterDao {
    @Query("""
        SELECT * FROM NetworkFilterEntity 
        WHERE deviceId = :deviceId 
        AND packageName = :packageName
        AND columnName = :column
    """)
    suspend fun get(
        deviceId: String,
        packageName: String,
        column: NetworkTextFilterColumns
    ): NetworkFilterEntity?

    @Query("""
        SELECT * FROM NetworkFilterEntity 
        WHERE deviceId = :deviceId
         AND packageName = :packageName
        """)
    fun observe(
        deviceId: String,
        packageName: String
    ): Flow<List<NetworkFilterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: NetworkFilterEntity)
}
