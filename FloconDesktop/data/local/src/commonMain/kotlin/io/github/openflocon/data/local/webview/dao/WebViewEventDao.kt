package io.github.openflocon.data.local.webview.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.github.openflocon.data.local.webview.models.WebViewEventEntity

@Dao
interface WebViewEventDao {
    @Upsert
    suspend fun insertAll(events: List<WebViewEventEntity>)

    @Query(
        """
        SELECT * FROM WebViewEventEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
        AND (:kind IS NULL OR kind = :kind)
        AND (:filter IS NULL
            OR label LIKE '%' || :filter || '%'
            OR message LIKE '%' || :filter || '%'
            OR url LIKE '%' || :filter || '%'
            OR webViewId LIKE '%' || :filter || '%')
        ORDER BY timestamp DESC
    """
    )
    fun observe(
        deviceId: String,
        packageName: String,
        kind: String?,
        filter: String?,
    ): PagingSource<Int, WebViewEventEntity>

    @Query(
        """
        SELECT * FROM WebViewEventEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
        AND (:kind IS NULL OR kind = :kind)
        AND (:sinceTimestamp IS NULL OR timestamp >= :sinceTimestamp)
        AND (:filter IS NULL
            OR label LIKE '%' || :filter || '%'
            OR message LIKE '%' || :filter || '%'
            OR url LIKE '%' || :filter || '%'
            OR webViewId LIKE '%' || :filter || '%')
        ORDER BY
            CASE WHEN :newestFirst = 1 THEN timestamp END DESC,
            CASE WHEN :newestFirst = 0 THEN timestamp END ASC
        LIMIT :limit
    """
    )
    suspend fun search(
        deviceId: String,
        packageName: String,
        kind: String?,
        sinceTimestamp: Long?,
        filter: String?,
        newestFirst: Boolean,
        limit: Int,
    ): List<WebViewEventEntity>

    @Query(
        """
        DELETE FROM WebViewEventEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
    """
    )
    suspend fun clearAll(
        deviceId: String,
        packageName: String,
    )
}
