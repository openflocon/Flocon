package com.florent37.flocondesktop.features.graphql.ui.view.header

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.florent37.flocondesktop.common.ui.FloconColors
import com.florent37.flocondesktop.features.graphql.ui.view.GraphQlItemColumnWidths
import com.florent37.flocondesktop.features.network.ui.view.components.HeaderLabelItem

@Composable
fun GraphQlItemHeaderView(
    columnWidths: GraphQlItemColumnWidths = GraphQlItemColumnWidths(), // Default widths provided
    modifier: Modifier = Modifier,
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
            modifier = Modifier.width(columnWidths.dateWidth),
            text = "Request Time",
        )
        HeaderLabelItem(
            modifier = Modifier.width(columnWidths.methodWidth),
            text = "Method",
        )
        HeaderLabelItem(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.TopStart,
            text = "Route",
        )
        HeaderLabelItem(
            modifier = Modifier.width(columnWidths.statusCodeWidth),
            text = "Status",
        )
        HeaderLabelItem(
            modifier = Modifier.width(columnWidths.requestSizeWidth),
            text = "Request Size",
        )
        HeaderLabelItem(
            modifier = Modifier.width(columnWidths.responseSizeWidth),
            text = "Response Size",
        )
        HeaderLabelItem(
            modifier = Modifier.width(columnWidths.timeWidth),
            text = "Time",
        )
    }
}
