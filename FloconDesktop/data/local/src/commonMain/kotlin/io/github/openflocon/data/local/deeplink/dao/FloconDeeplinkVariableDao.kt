package io.github.openflocon.data.local.deeplink.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import io.github.openflocon.data.local.deeplink.models.DeeplinkVariableEntity
import io.github.openflocon.domain.device.models.DeviceId
import kotlinx.coroutines.flow.Flow

@Dao
interface FloconDeeplinkVariableDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(deeplink: DeeplinkVariableEntity)

    @Query(
        """
       DELETE FROM DeeplinkVariableEntity
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
        variables: List<DeeplinkVariableEntity>,
    ) {
        deleteAll(deviceId = deviceId, packageName = packageName)
        variables.forEach { insert(deeplink = it) }
    }

    @Query(
        """
            SELECT *
            FROM DeeplinkVariableEntity
            WHERE deviceId = :deviceId
            AND packageName = :packageName
            AND isHistory = false
            ORDER BY id ASC
            """,
    )
    fun observeAll(deviceId: String, packageName: String): Flow<List<DeeplinkVariableEntity>>

    @Query(
        """
            SELECT *
            FROM DeeplinkVariableEntity
            WHERE deviceId = :deviceId
            AND packageName = :packageName
            AND isHistory = true
            ORDER BY id DESC
            """,
    )
    fun observeHistory(deviceId: String, packageName: String): Flow<List<DeeplinkVariableEntity>>

    @Query(
        """
            DELETE
            FROM DeeplinkVariableEntity
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
            FROM DeeplinkVariableEntity
            WHERE deviceId = :deviceId
            AND packageName = :packageName
            AND id = :deeplinkId
            LIMIT 1
            """,
    )
    suspend fun getById(deviceId: String, packageName: String, deeplinkId: Long): DeeplinkVariableEntity?
}
