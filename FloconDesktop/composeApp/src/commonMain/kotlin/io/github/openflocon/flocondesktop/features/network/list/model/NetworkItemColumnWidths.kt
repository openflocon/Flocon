package io.github.openflocon.flocondesktop.features.network.list.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Data class to define the fixed widths for each column in NetworkItemView.
 * This allows for easy configuration and consistency across all items in a LazyColumn.
 */
data class NetworkItemColumnWidths(
    val dateWidth: Dp = 100.dp,
    val methodWidth: Dp = 90.dp,
    val domainWeight: Float = 1f,
    val queryWeight: Float = 2f,
    val statusCodeWidth: Dp = 90.dp,
    val timeWidth: Dp = 90.dp,
    // The 'route' column will use Modifier.weight(1f) to take remaining space
)
