package io.github.openflocon.data.local.timeline.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.github.openflocon.data.local.timeline.models.TimelineRowEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * The pages of the timeline: one query per kind of event, each limited to a page and starting after the last event loaded
 * (a keyset, not an offset), merged here. A page costs the same wherever it starts, and no query sorts all the events.
 *
 * @param pageQueries the queries of a page, one per kind: see [buildTimelinePageQueries]
 * @param fetch runs one of them
 */
internal class TimelineKeysetPagingSource(
    private val pageQueries: (cursor: TimelineCursor?, forward: Boolean, limit: Int) -> List<TimelineQuery>,
    private val fetch: suspend (TimelineQuery) -> List<TimelineRowEntity>,
) : PagingSource<TimelineCursor, TimelineRowEntity>() {

    override val jumpingSupported: Boolean = false

    /**
     * The page is reloaded from the event above the one the user looks at, the events above it are loaded back when scrolling up.
     * At the top of the list: from the newest event, to show the ones that arrived.
     */
    override fun getRefreshKey(state: PagingState<TimelineCursor, TimelineRowEntity>): TimelineCursor? {
        val anchor = state.anchorPosition ?: return null
        if (anchor <= 0) return null
        return state.closestItemToPosition(anchor - 1)?.cursor()
    }

    override suspend fun load(params: LoadParams<TimelineCursor>): LoadResult<TimelineCursor, TimelineRowEntity> = try {
        val limit = params.loadSize
        when (params) {
            is LoadParams.Prepend -> {
                val rows = loadPage(params.key, forward = false, limit = limit)
                LoadResult.Page(
                    data = rows,
                    prevKey = rows.firstOrNull()?.cursor()?.takeIf { rows.size >= limit },
                    nextKey = null,
                )
            }
            is LoadParams.Append -> {
                val rows = loadPage(params.key, forward = true, limit = limit)
                LoadResult.Page(
                    data = rows,
                    prevKey = null,
                    nextKey = rows.lastOrNull()?.cursor()?.takeIf { rows.size >= limit },
                )
            }
            is LoadParams.Refresh -> {
                // the event the page follows has gone: start again from the newest one
                val key = params.key
                var rows = loadPage(key, forward = true, limit = limit)
                val from = if (rows.isEmpty()) null else key
                if (rows.isEmpty() && key != null) rows = loadPage(null, forward = true, limit = limit)
                LoadResult.Page(
                    data = rows,
                    prevKey = from?.let { rows.firstOrNull()?.cursor() },
                    nextKey = rows.lastOrNull()?.cursor()?.takeIf { rows.size >= limit },
                )
            }
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        LoadResult.Error(e)
    }

    /**
     * At most [limit] events following the [cursor], newest first. Backward: the ones preceding it, the nearest of them.
     * Each query returns up to [limit] events, so the first [limit] of the merge are the ones of the union.
     */
    private suspend fun loadPage(cursor: TimelineCursor?, forward: Boolean, limit: Int): List<TimelineRowEntity> {
        val merged = coroutineScope {
            pageQueries(cursor, forward, limit).map { query -> async { fetch(query) } }.awaitAll()
        }.flatten().sortedWith(TimelineRowOrder)
        return if (forward) merged.take(limit) else merged.takeLast(limit)
    }
}
