package io.github.openflocon.flocondesktop.features.network.list.model

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val MinColumnWidth = 48.dp
private val MinRequestWidth = 160.dp

/**
 * Widths of the resizable columns in NetworkItem and its header, shared by all the items of the
 * LazyColumn. The request (path) column is not listed: it takes the remaining space.
 */
@Stable
class NetworkItemColumnWidths(
    dateWidth: Dp = 100.dp,
    methodWidth: Dp = 90.dp,
    domainWidth: Dp = 200.dp,
    statusCodeWidth: Dp = 104.dp,
    timeWidth: Dp = 100.dp,
) {
    var dateWidth by mutableStateOf(dateWidth)
        private set
    var methodWidth by mutableStateOf(methodWidth)
        private set
    var domainWidth by mutableStateOf(domainWidth)
        private set
    var statusCodeWidth by mutableStateOf(statusCodeWidth)
        private set
    var timeWidth by mutableStateOf(timeWidth)
        private set

    /** Width taken by the request column (the remaining space), reported by the header. */
    var requestWidth by mutableStateOf(Dp.Unspecified)

    fun resize(column: ResizableColumn, requestedDelta: Dp) {
        // a column can only grow while the request column keeps its minimum width
        val delta = if (requestedDelta > 0.dp && requestWidth != Dp.Unspecified) {
            requestedDelta.coerceAtMost((requestWidth - MinRequestWidth).coerceAtLeast(0.dp))
        } else {
            requestedDelta
        }
        when (column) {
            ResizableColumn.Date -> dateWidth = (dateWidth + delta).coerceAtLeast(MinColumnWidth)
            ResizableColumn.Method -> methodWidth = (methodWidth + delta).coerceAtLeast(MinColumnWidth)
            ResizableColumn.Domain -> domainWidth = (domainWidth + delta).coerceAtLeast(MinColumnWidth)
            ResizableColumn.Status -> statusCodeWidth = (statusCodeWidth + delta).coerceAtLeast(MinColumnWidth)
            ResizableColumn.Time -> timeWidth = (timeWidth + delta).coerceAtLeast(MinColumnWidth)
        }
        if (requestWidth != Dp.Unspecified) requestWidth -= delta
    }
}

enum class ResizableColumn {
    Date,
    Method,
    Domain,
    Status,
    Time,
}
