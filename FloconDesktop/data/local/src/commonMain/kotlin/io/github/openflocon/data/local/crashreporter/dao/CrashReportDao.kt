package io.github.openflocon.data.local.crashreporter.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.github.openflocon.data.local.crashreporter.models.CrashReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CrashReportDao {
    @Upsert
    suspend fun insertAll(crashes: List<CrashReportEntity>)

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM CrashReportEntity
            WHERE deviceId = :deviceId
            AND packageName = :packageName
        )
    """
    )
    fun observeHasAny(
        deviceId: String,
        packageName: String,
    ): Flow<Boolean>

    @Query(
        """
        SELECT * 
        FROM CrashReportEntity 
        WHERE crashId = :id
        AND deviceId = :deviceId
        AND packageName = :packageName
    """
    )
    fun observeCrashReportById(
        id: String,
        deviceId: String,
        packageName: String,
    ): Flow<CrashReportEntity?>

    @Query(
        """
        SELECT * FROM CrashReportEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
        ORDER BY timestamp DESC
    """
    )
    fun observeAll(
        deviceId: String,
        packageName: String,
    ): PagingSource<Int, CrashReportEntity>

    @Query(
        """
        SELECT * FROM CrashReportEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
        AND (:sinceTimestamp IS NULL OR timestamp >= :sinceTimestamp)
        AND (:filter IS NULL
            OR exceptionType LIKE '%' || :filter || '%'
            OR exceptionMessage LIKE '%' || :filter || '%'
            OR stackTrace LIKE '%' || :filter || '%')
        ORDER BY
            CASE WHEN :newestFirst = 1 THEN timestamp END DESC,
            CASE WHEN :newestFirst = 0 THEN timestamp END ASC
        LIMIT :limit
    """
    )
    suspend fun search(
        deviceId: String,
        packageName: String,
        sinceTimestamp: Long?,
        filter: String?,
        newestFirst: Boolean,
        limit: Int,
    ): List<CrashReportEntity>

    @Query("DELETE FROM CrashReportEntity WHERE crashId = :crashId")
    suspend fun delete(crashId: String)

    @Query(
        """
        DELETE FROM CrashReportEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
    """
    )
    suspend fun clearAll(
        deviceId: String,
        packageName: String,
    )
}
