package io.github.openflocon.data.local.timeline.datasource

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.testing.asSnapshot
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DistinctPagingDataTest {

    /**
     * A limit / offset source like the Room one: [inserted] events are added at the top after the first page is loaded,
     * before the source sees the invalidation, so the next page starts with events the first one already returned.
     */
    private class ShiftingSource(private val inserted: Int) : PagingSource<Int, String>() {
        private val events = MutableList(10) { "e$it" }

        override fun getRefreshKey(state: PagingState<Int, String>): Int? = null

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, String> {
            val offset = params.key ?: 0
            val page = events.drop(offset).take(params.loadSize)
            if (offset == 0) repeat(inserted) { events.add(0, "new$it") }
            return LoadResult.Page(
                data = page,
                prevKey = null,
                nextKey = if (offset + page.size >= events.size) null else offset + page.size,
            )
        }
    }

    private suspend fun load(inserted: Int, distinct: Boolean): List<String> = Pager(
        config = PagingConfig(pageSize = 4, initialLoadSize = 4, prefetchDistance = 1),
        pagingSourceFactory = { ShiftingSource(inserted) },
    ).flow
        .map { pagingData -> if (distinct) pagingData.distinctBy { it } else pagingData }
        .asSnapshot { appendScrollWhile { it != "e9" } }

    @Test
    fun `the source returns an event twice when one is inserted between two loads`() = runTest {
        val items = load(inserted = 1, distinct = false)

        assertEquals(listOf("e0", "e1", "e2", "e3", "e3"), items.take(5))
    }

    @Test
    fun `an event loaded twice is kept once`() = runTest {
        val items = load(inserted = 1, distinct = true)

        assertEquals((0 until 10).map { "e$it" }, items)
    }

    @Test
    fun `events loaded twice and not next to each other are kept once`() = runTest {
        val items = load(inserted = 3, distinct = true)

        assertEquals((0 until 10).map { "e$it" }, items)
    }
}
