package io.github.openflocon.data.local.network.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.openflocon.data.local.network.models.badquality.BadQualityConfigEntity
import io.github.openflocon.domain.device.models.DeviceId
import kotlinx.coroutines.flow.Flow

@Dao
interface NetworkBadQualityConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(config: BadQualityConfigEntity)

    @Query("""
        SELECT * 
        FROM BadQualityConfigEntity 
        WHERE deviceId = :deviceId AND packageName = :packageName
        AND id = :configId
        LIMIT 1
    """)
    suspend fun get(deviceId: String, packageName: String, configId: String): BadQualityConfigEntity?

    @Query("""
        SELECT * 
        FROM BadQualityConfigEntity 
        WHERE deviceId = :deviceId AND packageName = :packageName
        AND isEnabled = 1
        LIMIT 1
    """)
    suspend fun getTheOnlyEnabledNetworkQuality(deviceId: DeviceId, packageName: String) : BadQualityConfigEntity?

    @Query("""
        SELECT * 
        FROM BadQualityConfigEntity 
        WHERE deviceId = :deviceId AND packageName = :packageName
        AND id = :configId
        LIMIT 1
    """)
    fun observe(deviceId: String, packageName: String, configId: String): Flow<BadQualityConfigEntity?>

    @Query("""
        SELECT * 
        FROM BadQualityConfigEntity 
        WHERE deviceId = :deviceId AND packageName = :packageName
        ORDER BY createdAt
    """)
    fun observeAll(deviceId: String, packageName: String): Flow<List<BadQualityConfigEntity>>

    @Query("""
        UPDATE BadQualityConfigEntity
        SET isEnabled = CASE 
            WHEN id = :configId THEN 1 
            ELSE 0 
        END
        WHERE deviceId = :deviceId 
        AND packageName = :packageName
    """)
    suspend fun setEnabledConfig(
        deviceId: String,
        packageName: String,
        configId: String?,
    )

    @Query("""
        DELETE FROM BadQualityConfigEntity
        WHERE deviceId = :deviceId AND packageName = :packageName
        AND id = :configId
    """
    )
    suspend fun delete(deviceId: DeviceId, packageName: String, configId: String)
}
