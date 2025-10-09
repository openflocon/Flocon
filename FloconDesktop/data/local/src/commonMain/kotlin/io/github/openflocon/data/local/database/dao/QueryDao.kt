package io.github.openflocon.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import io.github.openflocon.data.local.database.models.FavoriteQueryEntity
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

    @Upsert
    suspend fun insertFavoriteQuery(query: FavoriteQueryEntity)

    @Query(
        """
        SELECT *
        FROM FavoriteQueryEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
        AND databaseId = :databaseId
        AND title = :title
        LIMIT 1
    """
    )
    suspend fun getFavoriteQueryByTitle(
        deviceId: String,
        packageName: String,
        databaseId: String,
        title: String,
    ): FavoriteQueryEntity?

    @Query(
        """
        SELECT *
        FROM FavoriteQueryEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
        ORDER BY timestamp DESC
    """
    )
    fun observeFavoriteQueryEntity(
        deviceId: String,
        packageName: String,
    ): Flow<List<FavoriteQueryEntity>>

    @Query(
        """
        SELECT *
        FROM FavoriteQueryEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
        AND id = :id
        LIMIT 1
    """
    )
    suspend fun getFavorite(
        deviceId: String,
        packageName: String,
        id: Long,
    ): FavoriteQueryEntity?

    @Query(
        """
        DELETE FROM FavoriteQueryEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
        AND databaseId = :databaseId
        AND id = :favoriteId
    """
    )
    suspend fun deleteFavoriteQuery(
        deviceId: String,
        packageName: String,
        databaseId: String,
        favoriteId: Long,
    )
}
