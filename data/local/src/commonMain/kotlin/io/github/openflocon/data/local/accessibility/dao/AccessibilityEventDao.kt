package io.github.openflocon.data.local.accessibility.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.openflocon.data.local.accessibility.models.AccessibilityEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccessibilityEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(entities: List<AccessibilityEventEntity>)

    @Query(
        """
        SELECT *
        FROM AccessibilityEventEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
        AND (:type IS NULL OR type = :type COLLATE NOCASE)
        AND (:text IS NULL
            OR text LIKE '%' || :text || '%'
            OR contentDescription LIKE '%' || :text || '%'
            OR className LIKE '%' || :text || '%'
            OR screen LIKE '%' || :text || '%')
        ORDER BY createdAt DESC, id DESC
        LIMIT :limit
    """,
    )
    fun observeEvents(
        deviceId: String,
        packageName: String,
        type: String?,
        text: String?,
        limit: Int,
    ): Flow<List<AccessibilityEventEntity>>

    @Query(
        """
        SELECT *
        FROM AccessibilityEventEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
        AND (:type IS NULL OR type = :type COLLATE NOCASE)
        AND (:text IS NULL
            OR text LIKE '%' || :text || '%'
            OR contentDescription LIKE '%' || :text || '%'
            OR className LIKE '%' || :text || '%'
            OR screen LIKE '%' || :text || '%')
        ORDER BY createdAt DESC, id DESC
        LIMIT :limit
    """,
    )
    suspend fun searchEvents(
        deviceId: String,
        packageName: String,
        type: String?,
        text: String?,
        limit: Int,
    ): List<AccessibilityEventEntity>

    @Query(
        """
        SELECT *
        FROM AccessibilityEventEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
        AND id = :id
        LIMIT 1
    """,
    )
    fun observeEvent(
        deviceId: String,
        packageName: String,
        id: String,
    ): Flow<AccessibilityEventEntity?>

    @Query(
        """
        SELECT DISTINCT type
        FROM AccessibilityEventEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
        ORDER BY type ASC
    """,
    )
    fun observeTypes(
        deviceId: String,
        packageName: String,
    ): Flow<List<String>>

    @Query(
        """
        SELECT DISTINCT type
        FROM AccessibilityEventEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
        ORDER BY type ASC
    """,
    )
    suspend fun getTypes(
        deviceId: String,
        packageName: String,
    ): List<String>

    @Query(
        """
        DELETE FROM AccessibilityEventEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
    """,
    )
    suspend fun deleteEvents(
        deviceId: String,
        packageName: String,
    ): Int
}
