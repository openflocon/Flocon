package io.github.openflocon.data.local.timeline.datasource

import androidx.paging.PagingConfig
import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import io.github.openflocon.data.local.timeline.models.TimelineRowEntity
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class TimelineKeysetPagingSourceTest {

    private fun row(kind: String, id: String, ts: Long) = TimelineRowEntity(
        kind = kind,
        id = id,
        ts = ts,
        title = "",
        subtitle = null,
        appInstance = null,
        method = null,
        statusCode = null,
        durationMs = null,
        sessionStart = null,
    )

    private val network = (1..7).map { row(KIND_NETWORK, "n$it", ts = it * 10L) }
    private val logs = (1..7).map { row(KIND_LOG, "l$it", ts = it * 10L + 5) }
    private val database = (1..4).map { row(KIND_DATABASE, "d$it", ts = it * 20L) }
    private val all = (network + logs + database).sortedWith(TimelineRowOrder)

    // a query per kind that keeps the rows after (or before) the cursor, like the SQL of buildTimelinePageQueries
    private fun source(rows: List<TimelineRowEntity> = network + logs + database): TimelineKeysetPagingSource {
        val kinds = rows.map { it.kind }.distinct()
        return TimelineKeysetPagingSource(
            pageQueries = { cursor, forward, limit ->
                kinds.map { kind -> TimelineQuery(sql = kind, params = listOf(cursor ?: "", forward, limit)) }
            },
            fetch = { query ->
                val kind = query.sql
                val cursor = query.params[0] as? TimelineCursor
                val forward = query.params[1] as Boolean
                val limit = query.params[2] as Int
                val kindRows = rows.filter { it.kind == kind }.sortedWith(TimelineRowOrder)
                val cursorRow = cursor?.let { row(it.kind, it.id, it.ts) }
                when {
                    cursorRow == null -> kindRows.take(limit)
                    forward -> kindRows.filter { TimelineRowOrder.compare(it, cursorRow) > 0 }.take(limit)
                    else -> kindRows.filter { TimelineRowOrder.compare(it, cursorRow) < 0 }.takeLast(limit).reversed()
                }.let { if (forward || cursorRow == null) it else it.reversed() }
            },
        )
    }

    private fun refresh(key: TimelineCursor? = null, size: Int = 5) = LoadParams.Refresh(key, size, false)

    private suspend fun TimelineKeysetPagingSource.page(params: LoadParams<TimelineCursor>) = assertIs<LoadResult.Page<TimelineCursor, TimelineRowEntity>>(load(params))

    @Test
    fun `a page is the newest events of every kind merged`() = runTest {
        val page = source().page(refresh())

        assertEquals(all.take(5), page.data)
        assertNull(page.prevKey)
        assertEquals(all[4].cursor(), page.nextKey)
    }

    @Test
    fun `appending the pages gives every event once, in order`() = runTest {
        val source = source()
        val loaded = ArrayList<TimelineRowEntity>()
        var page = source.page(refresh())
        loaded += page.data
        while (page.nextKey != null) {
            page = source.page(LoadParams.Append(page.nextKey!!, 5, false))
            loaded += page.data
        }

        assertEquals(all, loaded)
    }

    @Test
    fun `the last page has no next key`() = runTest {
        val page = source().page(refresh(size = 100))

        assertEquals(all, page.data)
        assertNull(page.nextKey)
    }

    @Test
    fun `prepending loads the events above the first one, nearest first`() = runTest {
        val source = source()
        val start = all[8]

        val page = source.page(refresh(key = all[7].cursor()))
        assertEquals(start, page.data.first())
        assertEquals(start.cursor(), page.prevKey)

        val above = source.page(LoadParams.Prepend(page.prevKey!!, 5, false))
        assertEquals(all.subList(3, 8), above.data)
        assertEquals(all[3].cursor(), above.prevKey)
    }

    @Test
    fun `the first events have nothing above them`() = runTest {
        val above = source().page(LoadParams.Prepend(all[2].cursor(), 5, false))

        assertEquals(all.subList(0, 2), above.data)
        assertNull(above.prevKey)
    }

    @Test
    fun `an event the refresh key points at that is gone restarts from the newest`() = runTest {
        val page = source().page(refresh(key = TimelineCursor(ts = 0, kind = KIND_NETWORK, id = "gone")))

        assertEquals(all.take(5), page.data)
        assertNull(page.prevKey)
    }

    @Test
    fun `events of a same time of two kinds are all kept`() = runTest {
        val rows = listOf(
            row(KIND_NETWORK, "1", ts = 10),
            row(KIND_NETWORK_RESPONSE, "1", ts = 10),
            row(KIND_LOG, "1", ts = 10),
            row(KIND_DATABASE, "1", ts = 10),
            row(KIND_APP_START, "10", ts = 10),
        )
        val source = source(rows)
        val loaded = ArrayList<TimelineRowEntity>()
        var page = source.page(refresh(size = 2))
        loaded += page.data
        while (page.nextKey != null) {
            page = source.page(LoadParams.Append(page.nextKey!!, 2, false))
            loaded += page.data
        }

        assertEquals(rows.sortedWith(TimelineRowOrder), loaded)
        // the response is above its request, the app start below the events
        assertEquals(KIND_NETWORK_RESPONSE, loaded.first().kind)
        assertEquals(KIND_APP_START, loaded.last().kind)
    }

    @Test
    fun `the refresh key is the event above the one the user looks at`() {
        val source = source()
        val page = LoadResult.Page<TimelineCursor, TimelineRowEntity>(data = all.take(10), prevKey = null, nextKey = null)
        val state = { anchor: Int? -> PagingState(listOf(page), anchor, PagingConfig(pageSize = 5), 0) }

        assertNull(source.getRefreshKey(state(null)))
        assertNull(source.getRefreshKey(state(0)))
        assertEquals(all[3].cursor(), source.getRefreshKey(state(4)))
    }

    @Test
    fun `a failing query is an error, not a crash`() = runTest {
        val source = TimelineKeysetPagingSource(
            pageQueries = { _, _, _ -> listOf(TimelineQuery(sql = "", params = emptyList())) },
            fetch = { error("boom") },
        )

        assertIs<LoadResult.Error<TimelineCursor, TimelineRowEntity>>(source.load(refresh()))
    }
}
