package io.github.openflocon.data.local.network.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import io.github.openflocon.data.local.network.models.mock.MockNetworkEntity
import io.github.openflocon.domain.device.models.DeviceId
import kotlinx.coroutines.flow.Flow

@Dao
interface NetworkMocksDao {

    @Upsert
    suspend fun addMock(mock: MockNetworkEntity)

    @Query(
        """
        SELECT * FROM MockNetworkEntity 
        WHERE mockId = :mockId
        LIMIT 1
    """)
    suspend fun getMock(mockId: String) : MockNetworkEntity?

    @Query(
        """
        SELECT * FROM MockNetworkEntity 
        WHERE isEnabled = 1 
        AND (
            (deviceId = :deviceId AND packageName = :packageName)
            OR 
            (deviceId IS NULL)
        )
    """
    )
    suspend fun getAllEnabledMocks(deviceId: String, packageName: String): List<MockNetworkEntity>

    @Query(
        """
        SELECT * FROM MockNetworkEntity 
        WHERE (
            (deviceId = :deviceId AND packageName = :packageName)
            OR 
            (deviceId IS NULL)
        )
    """
    )
    fun observeAllMocks(deviceId: String, packageName: String): Flow<List<MockNetworkEntity>>

    @Query(
        """
        DELETE FROM MockNetworkEntity 
        WHERE mockId = :mockId
    """
    )
    suspend fun deleteMock(mockId: String)

    @Query(
        """
        UPDATE MockNetworkEntity 
        SET isEnabled = :isEnabled
        WHERE mockId = :mockId
    """
    )
    suspend fun updateMockIsEnabled(
        mockId: String,
        isEnabled: Boolean
    )

    @Query(
        """
        UPDATE MockNetworkEntity 
        SET deviceId = :deviceId, packageName = :packageName
        WHERE mockId = :mockId
    """
    )
    suspend fun updateMockDevice(
        mockId: String,
        deviceId: String?,
        packageName: String?
    )
}
