package io.github.openflocon.flocondesktop.features.timeline.graph

import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.timeline.model.previewTimelineRowUiModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GraphClusteringTest {

    private fun event(id: String, start: Long, duration: Long = 0L, lane: GraphLane = GraphLane.Database, status: Int? = null) = GraphEventUiModel(
        row = previewTimelineRowUiModel(source = lane.source, id = id, title = id),
        lane = lane,
        startMs = start,
        durationMs = duration,
        name = id,
        statusCode = status,
    )

    private fun model(events: List<GraphEventUiModel>, single: Boolean = false) = buildGraphUiModel(events, sessionDurationMs = 100_000L, single = single)

    private fun GraphUiModel.markerCount() = lanes.sumOf { lane -> lane.rows.sumOf { it.size } + lane.clusters.sumOf { it.size } }

    private fun GraphUiModel.allClusters() = lanes.flatMap { lane -> lane.clusters.flatten() }

    @Test
    fun `events far apart are not grouped`() {
        val events = List(5) { event("e$it", it * 1_000L) }
        // 0.1 px per ms: 100 px between two events
        val clustered = model(events).clustered(0.1)

        assertTrue(clustered.allClusters().isEmpty())
        assertEquals(5, clustered.markerCount())
    }

    @Test
    fun `100 events at the same time are one group in one row`() {
        val events = List(100) { event("e$it", 5_000L) }
        val clustered = model(events).clustered(0.1)

        assertEquals(1, clustered.rowCount)
        val cluster = clustered.allClusters().single()
        assertEquals(100, cluster.size)
        assertEquals("cluster:" + events.first().key, cluster.key)
        assertEquals(listOf(GraphLane.Database to 100), cluster.counts)
        // without grouping, 100 rows
        assertEquals(100, model(events).rowCount)
    }

    @Test
    fun `zooming in never reduces the number of markers`() {
        val events = (0 until 300).map { event("e$it", (it * 37L) % 4_000L, duration = if (it % 7 == 0) 80L else 0L) }
        val base = model(events)
        var previous = 0
        listOf(0.002, 0.01, 0.05, 0.1, 0.5, 1.0, 5.0, 20.0).forEach { px ->
            val count = base.clustered(px).markerCount()
            assertTrue(count >= previous, "$count markers at $px px/ms after $previous")
            previous = count
        }
    }

    @Test
    fun `a bar is grouped by its start, whatever its duration`() {
        // 200 ms at 0.1 px/ms is 20 px wide, but it starts with the others
        val events = listOf(event("bar", 1_000L, duration = 200L), event("a", 1_010L), event("b", 1_020L))
        val clustered = model(events).clustered(0.1)

        val cluster = clustered.allClusters().single()
        assertEquals(listOf("bar", "a", "b"), cluster.events.map { it.name })
        assertEquals(1, clustered.rowCount)
        // a bar that starts apart stays a bar
        val apart = model(events + event("late", 9_000L, duration = 200L)).clustered(0.1)
        assertEquals(listOf("late"), apart.lanes.flatMap { it.rows.flatten() }.map { it.name })
    }

    @Test
    fun `an expanded group puts its events back in the rows`() {
        val events = List(10) { event("e$it", 5_000L) }
        val grouped = model(events).clustered(0.1)
        val key = grouped.allClusters().single().key

        val expanded = model(events).clustered(0.1, expandedKeys = setOf(key))

        assertTrue(expanded.allClusters().isEmpty())
        assertEquals(10, expanded.rowCount)
        assertEquals(1, model(events).clustered(0.1, expandedKeys = setOf("other")).rowCount)
    }

    @Test
    fun `a chain of events every 10 px is cut into several groups`() {
        // 100 ms at 0.1 px/ms: 10 px between two events, a group spans at most 14 px from its first event
        val events = List(10) { event("e$it", it * 100L) }
        val clustered = model(events).clustered(0.1)

        val clusters = clustered.allClusters()
        assertEquals(5, clusters.size)
        assertEquals(List(5) { 2 }, clusters.map { it.size })
    }

    @Test
    fun `a lane has one source per group, the single graph mixes them in the order of the lanes`() {
        val events = listOf(
            event("d1", 100L, lane = GraphLane.Database),
            event("a1", 100L, lane = GraphLane.Analytics),
            event("a2", 101L, lane = GraphLane.Analytics),
            event("n1", 102L, lane = GraphLane.Network),
            event("d2", 103L, lane = GraphLane.Database),
        )

        val lanes = model(events).clustered(0.1)
        assertEquals(mapOf<GraphLane?, Int>(GraphLane.Analytics to 1, GraphLane.Network to 0, GraphLane.Database to 1), lanes.lanes.associate { it.lane to it.clusters.flatten().size })
        assertTrue(lanes.allClusters().all { it.counts.size == 1 })

        val single = model(events, single = true).clustered(0.1).allClusters().single()
        assertEquals(listOf(GraphLane.Analytics to 2, GraphLane.Network to 1, GraphLane.Database to 2), single.counts)
    }

    @Test
    fun `the segments fill the marker and none is under 8 percent`() {
        val events = List(99) { event("d$it", 100L) } + event("a", 100L, lane = GraphLane.Analytics)
        val cluster = model(events, single = true).clustered(0.1).allClusters().single()

        val width = clusterWidthPx(cluster, 0.1)
        val segments = clusterSegments(cluster, width)
        assertEquals(listOf(GraphLane.Analytics, GraphLane.Database), segments.map { it.lane })
        assertEquals(width, segments.sumOf { it.widthPx.toDouble() }.toFloat(), 0.01f)
        assertTrue(segments.all { it.widthPx >= width * ClusterMinSegmentRatio - 0.01f })
        // the segments follow the counts when none is small
        val even = clusterSegments(GraphClusterUiModel(cluster.events, listOf(GraphLane.Network to 3, GraphLane.Log to 1)), 100f)
        assertEquals(listOf(75f, 25f), even.map { it.widthPx })
    }

    @Test
    fun `the width follows the sources and the time spanned`() {
        val one = clusterEvents(listOf(event("a", 0L), event("b", 10L)), 0.1).clusters.single()
        assertEquals(ClusterMinWidthPx, clusterWidthPx(one, 0.1))
        val mixed = clusterEvents(listOf(event("a", 0L), event("b", 10L, lane = GraphLane.Log)), 0.1).clusters.single()
        assertEquals(ClusterMinWidthPx + ClusterSourceWidthPx, clusterWidthPx(mixed, 0.1))
        // 100 ms at 0.5 px/ms
        val long = GraphClusterUiModel(listOf(event("a", 0L), event("b", 100L)), listOf(GraphLane.Database to 2))
        assertEquals(50f + DiamondSizePx, clusterWidthPx(long, 0.5))
    }

    @Test
    fun `a click finds the group of its row`() {
        val events = List(5) { event("e$it", 1_000L) } + event("far", 50_000L, lane = GraphLane.Network)
        val clustered = model(events).clustered(0.1)
        val viewport = GraphViewport(startMs = 0.0, pxPerMs = 0.1)
        val tops = clustered.laneTops()
        val laneIndex = clustered.lanes.indexOfFirst { it.lane == GraphLane.Database }

        val x = viewport.xOf(1_000L)
        val hit = clustered.hitTestCluster(viewport, x, tops[laneIndex] + RowHeightPx / 2)
        assertNotNull(hit)
        assertEquals(5, hit.size)
        // the diamond is centered on the first event: the left half of the marker is hit too
        assertNotNull(clustered.hitTestCluster(viewport, x - 5f, tops[laneIndex] + 5f))
        assertNull(clustered.hitTestCluster(viewport, x + 200f, tops[laneIndex] + 5f))
        // the other lane has no group
        val networkIndex = clustered.lanes.indexOfFirst { it.lane == GraphLane.Network }
        assertNull(clustered.hitTestCluster(viewport, viewport.xOf(50_000L), tops[networkIndex] + 5f))
    }

    @Test
    fun `the row and the latest event are found inside a group`() {
        val events = listOf(event("first", 1_000L), event("second", 1_005L), event("alone", 9_000L), event("late", 20_000L), event("late2", 20_003L))
        val clustered = model(events).clustered(0.1)

        assertEquals(0f, clustered.rowTopOf(event("second", 1_005L)))
        assertEquals("late2", clustered.latestEvent()?.name)
        assertEquals(20_003L, clustered.latestMs())
        assertNull(clustered.rowTopOf(event("missing", 0L)))
    }

    @Test
    fun `the markers never overlap at the zoom of their bucket`() {
        val events = (0 until 400).map { event("e$it", (it * it * 13L) % 30_000L, duration = if (it % 11 == 0) 120L else 0L) }
        listOf(0.003, 0.02, 0.08, 0.3, 1.5).forEach { px ->
            val bucketPx = bucketPxPerMs(bucketOf(px))
            assertTrue(bucketPx <= px && px < bucketPx * 1.16)
            val clustered = model(events).clustered(bucketPx)
            // at the real zoom, which is up to 15 % higher than the one the markers were made for
            val spans = clustered.lanes.flatMap { lane ->
                lane.rows.indices.map { index ->
                    val bars = lane.rows[index].filter { !it.isInstant }.map { it.startMs * px to maxOf(it.endMs * px, it.startMs * px + MinBarWidthPx) }
                    val markers = lane.clusters.getOrElse(index) { emptyList() }.map {
                        val left = it.startMs * px - DiamondSizePx / 2
                        left to left + clusterWidthPx(it, px)
                    }
                    (bars + markers).sortedBy { it.first }
                }
            }
            spans.forEach { row ->
                row.zipWithNext().forEach { (a, b) -> assertTrue(a.second <= b.first + 0.01, "overlap at $px px/ms: $a $b") }
            }
        }
    }

    @Test
    fun `an unclustered model is unchanged and the buckets follow the zoom`() {
        val base = model(List(5) { event("e$it", it * 10_000L) })
        assertEquals(base.lanes, base.clustered(0.1).lanes)
        assertTrue(bucketOf(0.2) > bucketOf(0.1))
        assertEquals(bucketOf(0.1), bucketOf(0.1005))
    }

    @Test
    fun `an error in a group is counted`() {
        val cluster = clusterEvents(listOf(event("a", 0L, status = 500), event("b", 1L, status = 200), event("c", 2L)), 0.1).clusters.single()
        assertEquals(1, cluster.errorCount)
        assertEquals(TimelineSource.Database, cluster.counts.single().first.source)
    }

    @Test
    fun `the popover list keeps the events of the active sources in order`() {
        val cluster = clusterEvents(
            listOf(event("a", 0L), event("b", 1L, lane = GraphLane.Log), event("c", 2L)),
            0.1,
        ).clusters.single()

        assertEquals(listOf("a", "c"), filterClusterEvents(cluster.events, setOf(GraphLane.Database)).map { it.name })
        assertEquals(listOf("a", "b", "c"), filterClusterEvents(cluster.events, GraphLane.entries.toSet()).map { it.name })
        assertTrue(filterClusterEvents(cluster.events, emptySet()).isEmpty())
    }
}
