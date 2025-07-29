package com.florent37.flocondesktop.features.database.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QueryDao {
    @Insert
    suspend fun insertSuccessQuery(query: SuccessQueryEntity)

    @Query(
        """
        SELECT DISTINCT queryString
        FROM SuccessQueryEntity 
        WHERE deviceId = :deviceId 
        AND databaseId = :databaseId 
        ORDER BY timestamp DESC
    """,
    )
    fun observeSuccessQueriesByDeviceId(deviceId: String, databaseId: String): Flow<List<String>>

    @Query(
        """
        SELECT EXISTS(
            SELECT 1
            FROM SuccessQueryEntity 
            WHERE queryString = :queryString 
            AND databaseId = :databaseId 
            AND deviceId = :deviceId 
        )
    """,
    )
    suspend fun doesQueryExists(
        deviceId: String,
        databaseId: String,
        queryString: String,
    ): Boolean

    @Query(
        """
        DELETE FROM SuccessQueryEntity 
        WHERE deviceId = :deviceId 
        AND databaseId = :databaseId
        AND queryString = :queryString
    """,
    )
    suspend fun deleteQuery(
        deviceId: String,
        databaseId: String,
        queryString: String,
    )
}
