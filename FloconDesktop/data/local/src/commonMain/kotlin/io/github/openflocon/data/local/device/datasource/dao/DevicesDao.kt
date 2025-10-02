package io.github.openflocon.data.local.device.datasource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import io.github.openflocon.data.local.device.datasource.model.DeviceAppEntity
import io.github.openflocon.data.local.device.datasource.model.DeviceDisplayFpsEntity
import io.github.openflocon.data.local.device.datasource.model.DeviceEntity
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

@Dao
interface DevicesDao {

    @Query("SELECT * FROM DeviceEntity")
    fun observeDevices(): Flow<List<DeviceEntity>>

    @Query("SELECT * FROM DeviceEntity WHERE deviceId = :deviceId")
    fun observeDeviceById(deviceId: String): Flow<DeviceEntity?>

    @Query("SELECT * FROM DeviceEntity WHERE deviceId = :deviceId")
    suspend fun getDeviceById(deviceId: String): DeviceEntity?

    @Upsert
    suspend fun insertDevice(device: DeviceEntity)

    @Upsert
    suspend fun insertDeviceApp(app: DeviceAppEntity)

    @Query("DELETE FROM DeviceEntity")
    suspend fun clear()

    @Query(
        """
        SELECT * FROM DeviceAppEntity 
        WHERE deviceId = :deviceId
        """
    )
    fun observeDeviceApps(deviceId: String): Flow<List<DeviceAppEntity>>

    @Query(
        """
        SELECT * FROM DeviceAppEntity
        WHERE deviceId = :deviceId 
        AND packageName = :packageName
     """
    )
    suspend fun getDeviceAppByPackageName(
        deviceId: String,
        packageName: String
    ): DeviceAppEntity?

    @Query(
        """
        SELECT * FROM DeviceAppEntity
        WHERE deviceId = :deviceId 
        AND packageName = :packageName
     """
    )
    fun observeDeviceAppByPackageName(
        deviceId: String,
        packageName: String
    ): Flow<DeviceAppEntity?>

    @Query(
        """
        UPDATE DeviceAppEntity
        SET iconEncoded = :iconEncoded
        WHERE deviceId = :deviceId AND packageName = :packageName
        """
    )
    suspend fun updateAppIcon(deviceId: String, packageName: String, iconEncoded: String)

    @Query(
        """
        SELECT iconEncoded IS NOT NULL 
        FROM DeviceAppEntity
        WHERE deviceId = :deviceId 
        AND packageName = :appPackageName
     """
    )
    suspend fun hasAppIcon(deviceId: String, appPackageName: String): Boolean

    @Query("DELETE FROM DeviceEntity WHERE deviceId = :deviceId")
    suspend fun deleteDevice(deviceId: String)

    @Query("DELETE FROM DeviceAppEntity WHERE deviceId = :deviceId AND packageName = :packageName")
    suspend fun deleteApp(deviceId: String, packageName: String)

    @Query(
        """
        SELECT * FROM DeviceDisplayFpsEntity
        WHERE deviceId = :deviceId AND packageName = :packageName
        LIMIT 1
     """
    )
    suspend fun getIsDeviceDisplayingFps(deviceId: String, packageName: String): DeviceDisplayFpsEntity?

    @Upsert
    suspend fun saveIsDeviceDisplayingFps(
        displayFps : DeviceDisplayFpsEntity
    )

    @Query(
        """
        SELECT * FROM DeviceDisplayFpsEntity
        WHERE deviceId = :deviceId AND packageName = :packageName
        LIMIT 1
     """
    )
    fun observeIsDeviceDisplayingFps(deviceId: String, packageName: String): Flow<DeviceDisplayFpsEntity?>
}
