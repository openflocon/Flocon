package io.github.openflocon.flocondesktop.features.deeplinks.data.datasource.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.features.deeplinks.data.datasource.room.model.DeeplinkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FloconDeeplinkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(deeplink: DeeplinkEntity)

    @Query(
        """
       DELETE FROM DeeplinkEntity
       WHERE deviceId = :deviceId 
    """,
    )
    suspend fun deleteAll(deviceId: String)

    @Transaction
    suspend fun updateAll(
        deviceId: DeviceId,
        deeplinks: List<DeeplinkEntity>,
    ) {
        deleteAll(deviceId)
        deeplinks.forEach { insert(it) }
    }

    @Query(
        """
            SELECT *
            FROM DeeplinkEntity
            WHERE deviceId = :deviceId
            ORDER BY id ASC
            """,
    )
    fun observeAll(deviceId: String): Flow<List<DeeplinkEntity>>
}
