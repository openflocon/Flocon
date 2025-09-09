package io.github.openflocon.flocondesktop.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.openflocon.library.designsystem.common.FloconContextMenuItem

// right click on desktop
@Composable
expect fun ContextualView(
    items: List<FloconContextMenuItem>,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
)
