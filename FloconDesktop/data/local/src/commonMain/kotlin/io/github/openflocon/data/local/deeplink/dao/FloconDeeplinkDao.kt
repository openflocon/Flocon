package io.github.openflocon.data.local.deeplink.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import io.github.openflocon.data.local.deeplink.models.DeeplinkEntity
import io.github.openflocon.domain.device.models.DeviceId
import kotlinx.coroutines.flow.Flow

@Dao
interface FloconDeeplinkDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(deeplink: DeeplinkEntity)

    @Query(
        """
       DELETE FROM DeeplinkEntity
       WHERE deviceId = :deviceId 
       AND packageName = :packageName
    """,
    )
    suspend fun deleteAll(deviceId: String, packageName: String)

    @Transaction
    suspend fun updateAll(
        deviceId: DeviceId,
        packageName: String,
        deeplinks: List<DeeplinkEntity>,
    ) {
        deleteAll(deviceId = deviceId, packageName = packageName)
        deeplinks.forEach { insert(deeplink = it) }
    }

    @Query(
        """
            SELECT *
            FROM DeeplinkEntity
            WHERE deviceId = :deviceId
            AND packageName = :packageName
            ORDER BY id ASC
            """,
    )
    fun observeAll(deviceId: String, packageName: String): Flow<List<DeeplinkEntity>>
}
