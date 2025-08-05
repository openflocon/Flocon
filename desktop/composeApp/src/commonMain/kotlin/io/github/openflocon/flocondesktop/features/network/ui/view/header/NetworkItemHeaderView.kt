package io.github.openflocon.flocondesktop.features.network.ui.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.network.ui.view.NetworkItemColumnWidths
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun NetworkItemHeaderView(
    columnWidths: NetworkItemColumnWidths = NetworkItemColumnWidths(), // Default widths provided
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    Row(
        modifier =
            modifier
                .background(FloconTheme.colorPalette.panel)
                .padding(horizontal = 8.dp, vertical = 4.dp) // Padding for the entire item
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        // Date - Fixed width from data class
        HeaderLabelItem(
            modifier = Modifier.width(columnWidths.dateWidth),
            text = "Request Time",
        )

        // route & method, don't display the label
        Box(Modifier.weight(1f))

        HeaderLabelItem(
            modifier = Modifier.width(columnWidths.statusCodeWidth),
            text = "Status",
        )
        /*
        HeaderLabelItem(
            modifier = Modifier.width(columnWidths.requestSizeWidth),
            text = "Request Size",
        )
        HeaderLabelItem(
            modifier = Modifier.width(columnWidths.responseSizeWidth),
            text = "Response Size",
       }
         */
        HeaderLabelItem(
            modifier = Modifier.width(columnWidths.timeWidth),
            text = "Time",
        )
    }
}
