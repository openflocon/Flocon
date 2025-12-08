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
