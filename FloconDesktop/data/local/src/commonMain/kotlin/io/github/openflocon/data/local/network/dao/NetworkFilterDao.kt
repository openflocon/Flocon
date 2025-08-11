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
    @Query("SELECT * FROM network_filter WHERE deviceId = :deviceId AND columnName = :column")
    suspend fun get(deviceId: String, column: NetworkTextFilterColumns): NetworkFilterEntity?

    @Query("SELECT * FROM network_filter WHERE deviceId = :deviceId")
    fun observe(deviceId: String): Flow<List<NetworkFilterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: NetworkFilterEntity)
}
