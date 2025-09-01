@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.composeunstyled.DropdownPanelAnchor
import com.composeunstyled.Text
import io.github.openflocon.library.designsystem.FloconTheme

private val DropdownItemHeight = 30.dp

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
                .padding(vertical = 4.dp)
                .width(width = 200.dp)
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
    val interaction = remember { MutableInteractionSource() }

    FloconDropdownMenuItem(
        modifier = Modifier
            .clickable(
                onClick = onClick,
                indication = LocalIndication.current,
                interactionSource = interaction,
            )
    ) {
        LeadingIcon(leadingIcon)
        Text(
            text = text,
            style = FloconTheme.typography.labelMedium
        )
    }
}

@Composable
fun FloconDropdownMenuItem(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null
) {
    val interaction = remember { MutableInteractionSource() }

    FloconDropdownMenuItem(
        modifier = modifier
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                indication = LocalIndication.current,
                interactionSource = interaction,
            )
    ) {
        LeadingIcon(leadingIcon)
        Text(
            text = text,
            style = FloconTheme.typography.labelMedium,
            modifier = Modifier.weight(1f)
        )
        FloconSwitch(
            checked = checked
        )
    }
}

@Composable
fun FloconDropdownSeparator(
    modifier: Modifier = Modifier
) {
    FloconHorizontalDivider(
        modifier = modifier.padding(horizontal = 4.dp)
    )
}

@Composable
private fun LeadingIcon(
    leadingIcon: ImageVector?
) {
    if (leadingIcon != null) {
        FloconIcon(
            imageVector = leadingIcon,
            modifier = Modifier.size(16.dp)
        )
    } else {
        Box(Modifier.size(16.dp))
    }
}

@Composable
private fun FloconDropdownMenuItem(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(DropdownItemHeight)
            .clip(FloconTheme.shapes.small)
            .then(modifier),
        content = content
    )
}
