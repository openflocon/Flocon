@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun FloconDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        containerColor = FloconTheme.colorPalette.panel,
        content = content
    )
}
