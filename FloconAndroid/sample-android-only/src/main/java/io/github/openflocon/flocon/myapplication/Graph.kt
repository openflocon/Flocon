package io.github.openflocon.flocon.myapplication

// NetworkTimelineVirtualized.kt
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import io.github.openflocon.flocon.myapplication.NetworkEvent.Type
import java.lang.Math.pow
import kotlin.math.max
import kotlin.math.roundToInt

// -----------------------------
// Data model
// -----------------------------
data class NetworkEvent(
    val id: String,
    val url: String,
    val startMs: Long,
    val durationMs: Long,
    val type: Type,
) {
    enum class Type {
        Network,
        Database,
    }
}

// -----------------------------
// Lane packing algorithm
// returns Map<laneIndex, List<NetworkEvent>>
// -----------------------------
fun computeLanes(events: List<NetworkEvent>): Map<Int, List<NetworkEvent>> {
    val sorted = events.sortedBy { it.startMs }
    val laneEndTimes = mutableListOf<Long>()
    val lanes = mutableMapOf<Int, MutableList<NetworkEvent>>()

    for (ev in sorted) {
        val start = ev.startMs
        var placed = false
        for (laneIdx in laneEndTimes.indices) {
            if (start >= laneEndTimes[laneIdx]) {
                laneEndTimes[laneIdx] = ev.startMs + ev.durationMs
                lanes.getOrPut(laneIdx) { mutableListOf() }.add(ev)
                placed = true
                break
            }
        }
        if (!placed) {
            laneEndTimes += ev.startMs + ev.durationMs
            val newIdx = laneEndTimes.size - 1
            lanes.getOrPut(newIdx) { mutableListOf() }.add(ev)
        }
    }
    return lanes.toSortedMap()
}

// -----------------------------
// Time ruler (simple)
// -----------------------------
@Composable
fun TimeRuler(minStart: Long, maxEnd: Long, scalePxPerMs: Float, viewportWidthPx: Int) {
    val totalMs = (maxEnd - minStart).coerceAtLeast(1L)
    // Choose step in ms such that tick spacing is readable
    val desiredPxPerTick = 120f
    val approxTickCount = max(1, (viewportWidthPx / desiredPxPerTick).toInt())
    val rawStepMs = totalMs.toFloat() / approxTickCount
    // round rawStepMs to 1/2/5 * 10^k
    val pow =
        10.0.pow(kotlin.math.floor(kotlin.math.log10(rawStepMs.toDouble())).toDouble()).toLong()
    val candidates = listOf(1L, 2L, 5L, 10L).map { it * pow }
    val stepMs = candidates.minByOrNull { kotlin.math.abs(it - rawStepMs) } ?: 100L

    // Render ticks in a row that will be placed inside a horizontally scrollable container.
    Row(
        modifier = Modifier
            .height(28.dp)
    ) {
        // We don't draw absolute positions here (Canvas will handle time coordinates per lane),
        // but we provide a simple visual ruler: ticks labeled with ms offset.
        // The caller will place this Row inside a container of width = total timeline width.
        // For simplicity in this implementation the ruler will be empty; the lane canvases show bars.
        // (You can implement a separate Canvas-based ruler if you want exact alignment.)
    }
}

// -----------------------------
// LaneCanvas: draw only visible events
// -----------------------------
@Composable
fun LaneCanvas(
    laneIndex: Int,
    events: List<NetworkEvent>,
    minStart: Long,
    scalePxPerMs: Float,
    timelineWidthPx: Int,
    viewportStartPx: Int,
    viewportWidthPx: Int,
    laneHeightDp: Dp,
    onEventClick: (NetworkEvent) -> Unit
) {
    val density = LocalDensity.current
    val laneHeightPx = with(density) { laneHeightDp.toPx() }

    val viewportEndPx = viewportWidthPx + viewportWidthPx

    val textMeasurer = rememberTextMeasurer()
// Apply the current text style from theme, otherwise TextStyle.Default will be used.
    val materialTextStyle = LocalTextStyle.current.copy(fontSize = 10.sp)

    Box(
        modifier = Modifier
            .height(laneHeightDp)
            .width(with(density) { timelineWidthPx.toDp() })
    ) {

        // --- 1) Canvas drawing ---
        Canvas(modifier = Modifier.fillMaxSize()) {

            // Alternating row background (optional)
            drawRect(
                color = if (laneIndex % 2 == 0) Color(0x00FFFFFF) else Color(0x08000000),
                size = size
            )

            events.forEach { ev ->
                val xStart = (ev.startMs - minStart) * scalePxPerMs
                val xEnd = (ev.startMs + ev.durationMs - minStart) * scalePxPerMs
                val width = max(2f, xEnd - xStart)

                // --- keep partial visibility ---
                val intersects = if (xStart < viewportStartPx) {
                    // draw inky if xEnd > viewportStartPx
                    xEnd > viewportStartPx
                } else if (xEnd > viewportEndPx) {
                    xStart < viewportEndPx
                } else {
                    // there's a visible part
                    (viewportStartPx < xEnd && xEnd < viewportEndPx) || (viewportStartPx < xStart && xStart < viewportEndPx)
                }

                if (!intersects) return@forEach

                val color = when(ev.type) {
                    NetworkEvent.Type.Network -> Color.Green
                    NetworkEvent.Type.Database -> Color(0xFFBBDEFB)
                }

                val size =  Size(width, laneHeightPx - 12f)
                val topLeft = Offset(xStart, 6f)

                drawRoundRect(
                    color = color,
                    topLeft = Offset(xStart, 6f),
                    size = size,
                    cornerRadius = CornerRadius(8f, 8f)
                )

                val textPaddingHorizontal = 10

                val textLayoutResult =
                    textMeasurer.measure(
                        text = ev.url,
                        style = materialTextStyle,
                        constraints =
                            Constraints.fixed(
                                width = size.width.toInt() - textPaddingHorizontal,
                                height = size.height.toInt()
                            ),
                        overflow = TextOverflow.Ellipsis
                    )
                drawText(textLayoutResult, topLeft = topLeft.copy(
                    x = topLeft.x + textPaddingHorizontal / 2f,
                    y = topLeft.y + 4f
                ))
            }
        }

        // --- 2) Click overlay ---
        events.forEach { ev ->
            val xStart = (ev.startMs - minStart) * scalePxPerMs
            val xEnd = (ev.startMs + ev.durationMs - minStart) * scalePxPerMs
            val width = max(2f, xEnd - xStart)

            val intersects =
                xEnd > viewportStartPx &&
                        xStart < viewportStartPx + viewportWidthPx

            if (!intersects) return@forEach

            Box(
                modifier = Modifier
                    .offset(x = with(density) { xStart.toDp() })
                    .width(with(density) { width.toDp() })
                    .height(laneHeightDp)
                    .clickable { onEventClick(ev) }
            )
        }
    }
}

// -----------------------------
// Main composable: virtualized 2D timeline
// -----------------------------
@Composable
fun NetworkTimelineVirtualized(
    events: List<NetworkEvent>,
    modifier: Modifier = Modifier
) {
    // Zoom: pixels per ms
    var scalePxPerMs by remember { mutableStateOf(0.6f) } // initial px per ms
    val minStart = events.minOfOrNull { it.startMs } ?: 0L
    val maxEnd = events.maxOfOrNull { it.startMs + it.durationMs } ?: 1000L
    val totalMs = (maxEnd - minStart).coerceAtLeast(1L)

    // lanes computed once when events change
    val lanesMap by remember(events) { mutableStateOf(computeLanes(events)) }
    val lanesCount = lanesMap.size

    // timeline total width in pixels (may be large)
    val timelineWidthPx = remember(scalePxPerMs, totalMs) {
        // ensure minimal width
        max(800, (totalMs * scalePxPerMs).toInt() + 200)
    }

    // global horizontal scroll state (pixels)
    val horizontalScroll = rememberScrollState(0)

    // We need to know viewport (container) width in px to compute visible range
    var viewportSize by remember { mutableStateOf(IntSize(0, 0)) }
    val viewportWidthPx = viewportSize.width
    val viewportStartPx = horizontalScroll.value

    // selection & popup
    var selectedEvent by remember { mutableStateOf<NetworkEvent?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // Controls
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Zoom", fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            Slider(
                value = scalePxPerMs,
                onValueChange = { scalePxPerMs = it },
                valueRange = 0.1f..3f,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(12.dp))
            Text("${"%.2f".format(scalePxPerMs)} px/ms", style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(8.dp))

        // Timeline container: horizontal scroll (global) + vertical lazy column (virtualized lanes)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { viewportSize = it } // capture viewport size
                .horizontalScroll(horizontalScroll) // horizontal panning for entire timeline
                .background(Color(0xFFFAFAFA))
        ) {
            // The width of the timeline content (in dp)
            val timelineWidthDp = with(LocalDensity.current) { timelineWidthPx.toDp() }
            // left column for labels (optional) — here simple lane numbers
            // main timeline area: virtualized vertical lanes
            // LazyColumn virtualizes lanes vertically
            LazyColumn(
                modifier = Modifier
                    .width(timelineWidthDp)
            ) {
                itemsIndexed(lanesMap.toList()) { idx, pair ->
                    val laneIndex = pair.first
                    val laneEvents = pair.second
                    // lane row: each one draws only visible events in Canvas
                    Box(
                        modifier = Modifier
                            .height(20.dp)
                            .width(timelineWidthDp)
                    ) {
                        LaneCanvas(
                            laneIndex = laneIndex,
                            events = laneEvents,
                            minStart = minStart,
                            scalePxPerMs = scalePxPerMs,
                            timelineWidthPx = timelineWidthPx,
                            viewportStartPx = viewportStartPx,
                            viewportWidthPx = viewportWidthPx,
                            laneHeightDp = 20.dp,
                            onEventClick = { selectedEvent = it }
                        )
                    }
                }
            }
        }
    }

    // Popup details
    selectedEvent?.let { ev ->
        Popup(onDismissRequest = { selectedEvent = null }) {
            Box(
                modifier = Modifier
                    .width(260.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(ev.url, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text("Start: ${ev.startMs} ms")
                    Text("Duration: ${ev.durationMs} ms")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { selectedEvent = null }) {
                        Text("Fermer")
                    }
                }
            }
        }
    }
}

// -----------------------------
// Preview
// -----------------------------
@Preview(showBackground = true, widthDp = 900, heightDp = 600)
@Composable
fun PreviewNetworkTimelineVirtualized() {
    val demoEvents = listOf(
        NetworkEvent("1", "/api/user", 0, 120, Type.Network),
        NetworkEvent("2", "/img/logo.png", 40, 300, Type.Network),
        NetworkEvent("3", "/api/data", 180, 90, Type.Network),
        NetworkEvent("4", "/api/slow", 500, 800, Type.Database),
        NetworkEvent("5", "/auth/login", 550, 200, Type.Network),
        NetworkEvent("6", "/items", 900, 150, Type.Database),
        NetworkEvent("7", "/sync", 1200, 350, Type.Network),
        NetworkEvent("8", "/products", 1300, 400, Type.Database),
        NetworkEvent("9", "/batch", 1310, 600, Type.Network),
        NetworkEvent("10", "/big", 2000, 1500, Type.Network),
        // add many events to simulate heavy load:
    ) + (11..400).map {
        val start = (it - 10) * 50L + (it % 7) * 20L
        NetworkEvent("$it", "/bulk/$it", start, (20 + (it % 10) * 30).toLong(), Type.Network)
    }

    NetworkTimelineVirtualized(events = demoEvents)
}

// -----------------------------
// Helpers
// -----------------------------
private fun Double.pow(exp: Double): Double = pow(this, exp)
