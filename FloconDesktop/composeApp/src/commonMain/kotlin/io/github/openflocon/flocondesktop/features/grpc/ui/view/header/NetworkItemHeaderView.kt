package io.github.openflocon.flocondesktop.features.grpc.ui.view.header

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.common.ui.FloconColors
import io.github.openflocon.flocondesktop.features.grpc.ui.model.GrpcItemColumnWidths
import io.github.openflocon.flocondesktop.features.network.ui.view.components.HeaderLabelItem

@Composable
fun GrpcItemHeaderView(
    modifier: Modifier = Modifier,
    columnWidths: GrpcItemColumnWidths = GrpcItemColumnWidths(), // Default widths provided
) {
    Row(
        modifier =
        modifier
            .background(FloconColors.pannel)
            .padding(horizontal = 8.dp, vertical = 4.dp) // Padding for the entire item
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        // Date - Fixed width from data class
        HeaderLabelItem(
            modifier = Modifier.width(columnWidths.requestTimeFormatted),
            text = "Request Time",
        )
        HeaderLabelItem(
            modifier = Modifier.weight(columnWidths.url),
            text = "Url",
        )
        HeaderLabelItem(
            modifier = Modifier.weight(columnWidths.method),
            contentAlignment = Alignment.TopStart,
            text = "Method",
        )
        HeaderLabelItem(
            modifier = Modifier.width(columnWidths.status),
            text = "Status",
        )
        HeaderLabelItem(
            modifier = Modifier.width(columnWidths.durationFormatted),
            text = "Duration",
        )
    }
}
