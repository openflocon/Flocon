package io.github.openflocon.data.local.deeplink.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import io.github.openflocon.data.local.deeplink.models.DeeplinkEntity
import io.github.openflocon.domain.device.models.AppPackageName
import io.github.openflocon.domain.device.models.DeviceId
import kotlinx.coroutines.flow.Flow

@Dao
interface FloconDeeplinkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(deeplink: DeeplinkEntity)

    @Query(
        """
       DELETE FROM DeeplinkEntity
       WHERE deviceId = :deviceId 
       AND packageName = :packageName
       AND isHistory = false
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
            AND isHistory = false
            ORDER BY id ASC
            """,
    )
    fun observeAll(deviceId: String, packageName: String): Flow<List<DeeplinkEntity>>

    @Query(
        """
            SELECT *
            FROM DeeplinkEntity
            WHERE deviceId = :deviceId
            AND packageName = :packageName
            AND isHistory = true
            ORDER BY id DESC
            """,
    )
    fun observeHistory(deviceId: String, packageName: String): Flow<List<DeeplinkEntity>>

    @Query(
        """
            DELETE
            FROM DeeplinkEntity
            WHERE deviceId = :deviceId
            AND id = :deeplinkId
            AND packageName = :packageName
            AND isHistory = :isHistory
            """,
    )
    suspend fun delete(deviceId: String, packageName: String, deeplinkId: Long, isHistory: Boolean)

    @Query(
        """
            SELECT *
            FROM DeeplinkEntity
            WHERE deviceId = :deviceId
            AND packageName = :packageName
            AND id = :deeplinkId
            LIMIT 1
            """,
    )
    suspend fun getById(deviceId: String, packageName: String, deeplinkId: Long) : DeeplinkEntity?
}
