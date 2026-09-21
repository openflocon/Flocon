package io.github.openflocon.flocondesktop.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.openflocon.library.designsystem.common.FloconContextMenuItem
import kotlinx.collections.immutable.ImmutableList

// right click on desktop
@Composable
expect fun ContextualView(
    items: ImmutableList<FloconContextMenuItem>,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
)
