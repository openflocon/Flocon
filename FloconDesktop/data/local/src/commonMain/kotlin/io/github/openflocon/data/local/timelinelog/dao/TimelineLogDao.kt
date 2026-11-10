package io.github.openflocon.data.local.timelinelog.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import io.github.openflocon.data.local.timelinelog.models.TimelineLogEntity
import kotlinx.coroutines.flow.Flow

const val MAX_TIMELINE_LOGS = 20_000

@Dao
interface TimelineLogDao {

    @Insert
    suspend fun insert(entity: TimelineLogEntity): Long

    /**
     * Keeps the newest [max] logs of the app, drops the others
     */
    @Query(
        """
        DELETE FROM TimelineLogEntity
        WHERE id IN (
            SELECT id
            FROM TimelineLogEntity
            WHERE deviceId = :deviceId
            AND packageName = :packageName
            ORDER BY timestamp DESC, id DESC
            LIMIT -1 OFFSET :max
        )
    """,
    )
    suspend fun trim(
        deviceId: String,
        packageName: String,
        max: Int,
    )

    @Transaction
    suspend fun insertAndTrim(
        entity: TimelineLogEntity,
        max: Int = MAX_TIMELINE_LOGS,
    ) {
        insert(entity)
        trim(
            deviceId = entity.deviceId,
            packageName = entity.packageName,
            max = max,
        )
    }

    @Query("SELECT * FROM TimelineLogEntity WHERE id = :id LIMIT 1")
    fun observe(id: Long): Flow<TimelineLogEntity?>

    @Query(
        """
        SELECT *
        FROM TimelineLogEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
        ORDER BY timestamp ASC, id ASC
    """,
    )
    suspend fun getAll(
        deviceId: String,
        packageName: String,
    ): List<TimelineLogEntity>
}
