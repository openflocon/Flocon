package io.github.openflocon.data.local.network.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.openflocon.data.local.network.models.mock.MockNetworkEntity
import io.github.openflocon.domain.device.models.DeviceId
import kotlinx.coroutines.flow.Flow

@Dao
interface NetworkMocksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMock(mock: MockNetworkEntity)

    @Query(
        """
        SELECT * FROM MockNetworkEntity 
        WHERE deviceId = :deviceId 
        AND packageName = :packageName 
        AND mockId = :id
        LIMIT 1
    """)
    suspend fun getMock(deviceId: DeviceId, packageName: String, id: String) : MockNetworkEntity?

    @Query(
        """
        SELECT * FROM MockNetworkEntity 
        WHERE deviceId = :deviceId 
        AND packageName = :packageName
    """
    )
    suspend fun getAllMocks(deviceId: String, packageName: String): List<MockNetworkEntity>

    @Query(
        """
        SELECT * FROM MockNetworkEntity 
        WHERE deviceId = :deviceId 
        AND packageName = :packageName
    """
    )
    fun observeAllMocks(deviceId: String, packageName: String): Flow<List<MockNetworkEntity>>

    @Query(
        """
        DELETE FROM MockNetworkEntity 
        WHERE deviceId = :deviceId 
        AND packageName = :packageName 
        AND mockId = :mockId
    """
    )
    suspend fun deleteMock(deviceId: String, packageName: String, mockId: String)
}
