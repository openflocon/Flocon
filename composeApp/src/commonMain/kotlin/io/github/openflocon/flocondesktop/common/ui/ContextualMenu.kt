package io.github.openflocon.flocondesktop.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.openflocon.library.designsystem.common.FloconContextMenuItem
import kotlinx.collections.immutable.ImmutableList

// right click on desktop
@Composable
expect fun ContextualMenu(
    items: ImmutableList<FloconContextMenuItem>,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
)

// right click on desktop, the items being built only when the menu opens: for rows of a long list
@Composable
expect fun ContextualMenu(
    itemsProvider: () -> ImmutableList<FloconContextMenuItem>,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
)
