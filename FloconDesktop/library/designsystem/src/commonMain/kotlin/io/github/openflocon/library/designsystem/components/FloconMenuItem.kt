package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

private val DropdownItemHeight = 30.dp
private val DropdownItemHorizontalPadding = 4.dp

@Composable
fun FloconMenuItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
    val interaction = remember { MutableInteractionSource() }

    FloconDropdownMenuItem(
        modifier = modifier
            .clickable(
                onClick = onClick,
                indication = LocalIndication.current,
                interactionSource = interaction,
            )
            .padding(horizontal = DropdownItemHorizontalPadding)
    ) {
        leadingIcon?.let { FloconMenuIcon(leadingIcon) } ?: run {
            Spacer(Modifier.width(4.dp))
        }
        Text(
            text = text,
            style = FloconTheme.typography.labelMedium,
            modifier = Modifier.weight(1f)
        )
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
        modifier = modifier
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                indication = LocalIndication.current,
                interactionSource = interaction,
            )
            .padding(PaddingValues(horizontal = DropdownItemHorizontalPadding))
    ) {
        FloconMenuIcon(leadingIcon)
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
fun FloconMenuSeparator(
    modifier: Modifier = Modifier
) {
    FloconHorizontalDivider(
        modifier = modifier.padding(horizontal = 4.dp),
        color = FloconTheme.colorPalette.secondary
    )
}

@Composable
private fun FloconMenuIcon(
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
