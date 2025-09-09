@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.composeunstyled.DropdownPanelAnchor
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun FloconDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = FloconTheme.colorPalette.primary,
    content: @Composable ColumnScope.() -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        containerColor = containerColor,
        content = content
    )
}

@Composable
fun FloconDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onExpandRequest: () -> Unit,
    anchorContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    anchor: DropdownPanelAnchor = DropdownPanelAnchor.BottomStart,
    content: @Composable ColumnScope.() -> Unit
) {
    com.composeunstyled.DropdownMenu(
        onExpandRequest = onExpandRequest,
        modifier = modifier
    ) {
        anchorContent()
        com.composeunstyled.DropdownMenuPanel(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            shape = FloconTheme.shapes.medium,
            backgroundColor = FloconTheme.colorPalette.primary,
            contentColor = FloconTheme.colorPalette.onPrimary,
            contentPadding = PaddingValues(2.dp),
            anchor = anchor,
            modifier = Modifier
                .padding(top = 8.dp)
                .menuBackground()
        ) {
            content()
        }
    }
}

@Composable
fun FloconDropdownMenuItem(
    text: String,
    onClick: () -> Unit,
    leadingIcon: ImageVector? = null
) {
    FloconMenuItem(
        text = text,
        onClick = onClick,
        leadingIcon = leadingIcon
    )
}

@Composable
fun FloconDropdownMenuItem(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null
) {
    FloconMenuItem(
        text = text,
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        leadingIcon = leadingIcon
    )
}

@Composable
fun FloconDropdownSeparator(
    modifier: Modifier = Modifier
) {
    FloconMenuSeparator(modifier = modifier)
}
