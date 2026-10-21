package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

private val DropdownItemHeight = 36.dp
private val DropdownItemHorizontalPadding = 10.dp

@Composable
fun FloconMenuItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryAction: (@Composable () -> Unit)? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
    val interaction = remember { MutableInteractionSource() }

    FloconDropdownMenuItem(
        interactionSource = interaction,
        modifier = modifier
            .clickable(
                onClick = onClick,
                // the hover is the feedback
                indication = null,
                interactionSource = interaction,
            )
            .padding(horizontal = DropdownItemHorizontalPadding)
    ) {
        leadingIcon?.let { FloconMenuIcon(leadingIcon) } ?: run {
            Spacer(Modifier.width(FloconTheme.spacing.extraSmall))
        }
        MenuItemText(text)
        secondaryAction?.invoke()
        trailingIcon?.let { FloconMenuIcon(trailingIcon) }
    }
}

@Composable
fun FloconMenuItem(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null
) {
    val interaction = remember { MutableInteractionSource() }

    FloconDropdownMenuItem(
        interactionSource = interaction,
        modifier = modifier
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                indication = null,
                interactionSource = interaction,
            )
            .padding(PaddingValues(horizontal = DropdownItemHorizontalPadding))
    ) {
        FloconMenuIcon(leadingIcon)
        MenuItemText(text)
        FloconSwitch(
            checked = checked
        )
    }
}

@Composable
fun FloconMenuSeparator(
    modifier: Modifier = Modifier
) {
    FloconHorizontalDivider(
        modifier = modifier.padding(horizontal = FloconTheme.spacing.extraSmall, vertical = FloconTheme.spacing.compact),
        color = FloconTheme.colors.line
    )
}

@Composable
private fun RowScope.MenuItemText(text: String) {
    Text(
        text = text,
        style = FloconTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium,
        color = FloconTheme.colors.ink,
        modifier = Modifier.weight(1f)
    )
}

@Composable
private fun FloconMenuIcon(
    leadingIcon: ImageVector?
) {
    if (leadingIcon != null) {
        FloconIcon(
            imageVector = leadingIcon,
            tint = FloconTheme.colors.inkMuted,
            modifier = Modifier.size(16.dp)
        )
    } else {
        Box(Modifier.size(16.dp))
    }
}

@Composable
private fun FloconDropdownMenuItem(
    interactionSource: MutableInteractionSource,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val hovered by interactionSource.collectIsHoveredAsState()

    Row(
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(DropdownItemHeight * FloconTheme.scale)
            .clip(FloconTheme.shapes.medium)
            .background(if (hovered) FloconTheme.colors.bg300 else Color.Transparent)
            .hoverable(interactionSource)
            .then(modifier),
        content = content
    )
}
