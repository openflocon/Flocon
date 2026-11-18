package io.github.openflocon.data.local.featureflags.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import io.github.openflocon.data.local.featureflags.models.FeatureFlagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FeatureFlagDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeatureFlags(entities: List<FeatureFlagEntity>)

    @Query(
        """
        DELETE FROM FeatureFlagEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
        AND (provider = :provider OR appInstance != :appInstance)
    """,
    )
    suspend fun deleteProviderAndOldSessions(
        deviceId: String,
        packageName: String,
        provider: String,
        appInstance: Long,
    )

    @Transaction
    suspend fun replaceProvider(
        deviceId: String,
        packageName: String,
        provider: String,
        appInstance: Long,
        entities: List<FeatureFlagEntity>,
    ) {
        deleteProviderAndOldSessions(
            deviceId = deviceId,
            packageName = packageName,
            provider = provider,
            appInstance = appInstance,
        )
        insertFeatureFlags(entities)
    }

    @Query(
        """
        SELECT *
        FROM FeatureFlagEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
        AND provider = :provider
        AND (:filter IS NULL
            OR name LIKE '%' || :filter || '%'
            OR variant LIKE '%' || :filter || '%'
            OR payload LIKE '%' || :filter || '%')
        ORDER BY name ASC
    """,
    )
    fun observeFeatureFlags(
        deviceId: String,
        packageName: String,
        provider: String,
        filter: String?,
    ): Flow<List<FeatureFlagEntity>>

    @Query(
        """
        SELECT DISTINCT provider
        FROM FeatureFlagEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
        ORDER BY provider ASC
    """,
    )
    fun observeProviders(
        deviceId: String,
        packageName: String,
    ): Flow<List<String>>

    @Query(
        """
        SELECT *
        FROM FeatureFlagEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
        AND (:provider IS NULL OR provider = :provider)
        AND (:filter IS NULL
            OR name LIKE '%' || :filter || '%'
            OR variant LIKE '%' || :filter || '%'
            OR payload LIKE '%' || :filter || '%')
        ORDER BY provider ASC, name ASC
        LIMIT :limit
    """,
    )
    suspend fun searchFeatureFlags(
        deviceId: String,
        packageName: String,
        provider: String?,
        filter: String?,
        limit: Int,
    ): List<FeatureFlagEntity>

    @Query(
        """
        SELECT *
        FROM FeatureFlagEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
        AND provider = :provider
        AND name = :name
        LIMIT 1
    """,
    )
    fun observeFeatureFlag(
        deviceId: String,
        packageName: String,
        provider: String,
        name: String,
    ): Flow<FeatureFlagEntity?>

    @Query(
        """
        SELECT *
        FROM FeatureFlagEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
        AND provider = :provider
        AND name = :name
        LIMIT 1
    """,
    )
    suspend fun getFeatureFlag(
        deviceId: String,
        packageName: String,
        provider: String,
        name: String,
    ): FeatureFlagEntity?

    @Query(
        """
        SELECT DISTINCT provider
        FROM FeatureFlagEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
        ORDER BY provider ASC
    """,
    )
    suspend fun getProviders(
        deviceId: String,
        packageName: String,
    ): List<String>
}
