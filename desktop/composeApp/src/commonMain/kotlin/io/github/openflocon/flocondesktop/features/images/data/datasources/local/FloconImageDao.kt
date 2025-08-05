package io.github.openflocon.flocondesktop.features.images.data.datasources.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.openflocon.flocondesktop.features.images.data.datasources.local.model.DeviceImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FloconImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: DeviceImageEntity)

    @Query("SELECT * FROM DeviceImageEntity WHERE deviceId = :deviceId ORDER BY time DESC")
    fun observeImagesForDevice(deviceId: String): Flow<List<DeviceImageEntity>>

    @Query("SELECT * FROM DeviceImageEntity WHERE deviceId = :deviceId ORDER BY time DESC")
    suspend fun getImagesForDevice(deviceId: String): List<DeviceImageEntity>

    @Query("DELETE FROM DeviceImageEntity WHERE deviceId = :deviceId")
    suspend fun deleteAllImagesForDevice(deviceId: String)
}
