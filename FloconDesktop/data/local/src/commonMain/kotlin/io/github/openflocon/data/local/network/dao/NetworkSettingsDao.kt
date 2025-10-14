package io.github.openflocon.data.local.network.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.github.openflocon.data.local.network.models.NetworkSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NetworkSettingsDao {
    @Query(
        """
        SELECT * FROM NetworkSettingsEntity 
        WHERE deviceId = :deviceId 
        AND packageName = :packageName
    """
    )
    suspend fun get(
        deviceId: String,
        packageName: String,
    ): NetworkSettingsEntity?

    @Query(
        """
        SELECT * FROM NetworkSettingsEntity 
        WHERE deviceId = :deviceId
         AND packageName = :packageName
        """
    )
    fun observe(
        deviceId: String,
        packageName: String
    ): Flow<NetworkSettingsEntity?>

    @Upsert
    suspend fun insertOrUpdate(entity: NetworkSettingsEntity)
}
