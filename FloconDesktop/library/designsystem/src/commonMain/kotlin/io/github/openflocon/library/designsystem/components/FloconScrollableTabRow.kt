package io.github.openflocon.library.designsystem.components

import androidx.compose.material3.ScrollableTabRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FloconScrollableTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    tabs: @Composable () -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier,
        edgePadding = 0.dp,
        divider = {},
        tabs = tabs
    )
}
