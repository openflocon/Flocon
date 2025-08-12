package io.github.openflocon.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.github.openflocon.data.local.database.models.SuccessQueryEntity
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
        AND packageName = :packageName
        AND databaseId = :databaseId
        ORDER BY timestamp DESC
    """,
    )
    fun observeSuccessQueriesByDeviceId(
        deviceId: String,
        packageName: String,
        databaseId: String,
    ): Flow<List<String>>

    @Query(
        """
        SELECT EXISTS(
            SELECT 1
            FROM SuccessQueryEntity 
            WHERE queryString = :queryString 
            AND databaseId = :databaseId 
            AND deviceId = :deviceId 
            AND packageName = :packageName
        )
    """,
    )
    suspend fun doesQueryExists(
        deviceId: String,
        packageName: String,
        databaseId: String,
        queryString: String,
    ): Boolean

    @Query(
        """
        DELETE FROM SuccessQueryEntity 
        WHERE deviceId = :deviceId 
        AND databaseId = :databaseId
        AND queryString = :queryString
        AND packageName = :packageName
    """,
    )
    suspend fun deleteQuery(
        deviceId: String,
        packageName: String,
        databaseId: String,
        queryString: String,
    )
}
