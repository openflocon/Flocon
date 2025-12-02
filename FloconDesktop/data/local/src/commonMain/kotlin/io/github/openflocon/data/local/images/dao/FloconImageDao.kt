package io.github.openflocon.data.local.images.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.openflocon.data.local.images.models.DeviceImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FloconImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: DeviceImageEntity)

    @Query(
        """
        SELECT * 
        FROM DeviceImageEntity 
        WHERE deviceId = :deviceId 
        AND packageName = :packageName 
        ORDER BY time DESC
    """
    )
    fun observeImagesForDevice(deviceId: String, packageName: String): Flow<List<DeviceImageEntity>>

    @Query(
        """
        SELECT * 
        FROM DeviceImageEntity 
        WHERE deviceId = :deviceId 
        AND packageName = :packageName 
        ORDER BY time DESC
        """
    )
    suspend fun getImagesForDevice(deviceId: String, packageName: String): List<DeviceImageEntity>

    @Query(
        """
        DELETE FROM DeviceImageEntity 
        WHERE deviceId = :deviceId
        AND packageName = :packageName 
    """
    )
    suspend fun deleteAllImagesForDevice(deviceId: String, packageName: String)
}
