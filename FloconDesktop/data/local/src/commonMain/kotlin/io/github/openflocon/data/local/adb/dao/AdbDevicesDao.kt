package io.github.openflocon.data.local.adb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.openflocon.data.local.adb.model.DeviceWithSerialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AdbDevicesDao {
    @Query("""
        SELECT * 
        FROM DeviceWithSerialEntity
    """)
    fun getAll(): Flow<List<DeviceWithSerialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(device: DeviceWithSerialEntity)

    @Query("""
        SELECT * 
        FROM DeviceWithSerialEntity
        WHERE deviceId = :deviceId 
        LIMIT 1
    """)
    suspend fun getFromDeviceId(deviceId: String): DeviceWithSerialEntity?
}
