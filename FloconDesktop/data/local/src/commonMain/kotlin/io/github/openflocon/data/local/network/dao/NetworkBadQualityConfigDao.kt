package io.github.openflocon.data.local.network.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.openflocon.data.local.network.models.badquality.BadQualityConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NetworkBadQualityConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(config: BadQualityConfigEntity)

    @Query("""
        SELECT * 
        FROM BadQualityConfigEntity 
        WHERE deviceId = :deviceId AND packageName = :packageName
        LIMIT 1
    """)
    suspend fun get(deviceId: String, packageName: String): BadQualityConfigEntity?


    @Query("""
        SELECT * 
        FROM BadQualityConfigEntity 
        WHERE deviceId = :deviceId AND packageName = :packageName
        LIMIT 1
    """)
    fun observe(deviceId: String, packageName: String): Flow<BadQualityConfigEntity?>

    @Query("""
        UPDATE BadQualityConfigEntity
        SET isEnabled = :isEnabled 
        WHERE deviceId = :deviceId 
        AND packageName = :packageName
    """)
    suspend fun updateIsEnabled(deviceId: String, packageName: String, isEnabled: Boolean)
}
