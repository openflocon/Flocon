package io.github.openflocon.data.local.network.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.openflocon.data.local.network.models.mock.MockNetworkResponseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NetworkMocksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMock(mock: MockNetworkResponseEntity)

    @Query("""
        SELECT * FROM MockNetworkResponseEntity 
        WHERE deviceId = :deviceId 
        AND packageName = :packageName
    """)
    suspend fun getAllMocks(deviceId: String, packageName: String): List<MockNetworkResponseEntity>

    @Query("""
        SELECT * FROM MockNetworkResponseEntity 
        WHERE deviceId = :deviceId 
        AND packageName = :packageName
    """)
    fun observeAllMocks(deviceId: String, packageName: String): Flow<List<MockNetworkResponseEntity>>

    @Query("""
        DELETE FROM MockNetworkResponseEntity 
        WHERE deviceId = :deviceId 
        AND packageName = :packageName 
        AND mockId = :mockId
    """)
    suspend fun deleteMock(deviceId: String, packageName: String, mockId: String)
}
