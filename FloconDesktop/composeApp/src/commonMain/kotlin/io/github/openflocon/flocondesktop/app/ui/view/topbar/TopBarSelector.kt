package io.github.openflocon.flocondesktop.app.ui.view.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon

/** When true the selector sits inside a joined control: no border or rounded corners of its own. */
internal val LocalTopBarSelectorJoined = compositionLocalOf { false }

@Composable
internal fun TopBarSelector(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = FloconTheme.shapes.medium,
    contentPadding: PaddingValues = PaddingValues(),
    content: @Composable () -> Unit,
) {
    val colors = FloconTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val joined = LocalTopBarSelectorJoined.current

    Row(
        modifier = modifier
            .then(
                if (joined) {
                    Modifier.background(if (hovered) colors.bg200 else Color.Transparent)
                } else {
                    Modifier
                        .clip(shape)
                        .background(if (hovered) colors.bg200 else colors.bg100)
                        .border(1.dp, if (hovered) colors.lineStrong else colors.line, shape)
                },
            )
            .hoverable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier.weight(1f)
        ) {
            content()
        }
        FloconIcon(
            imageVector = Icons.Outlined.KeyboardArrowDown,
            tint = colors.inkFaint,
            modifier = Modifier
                .padding(end = FloconTheme.spacing.medium, start = FloconTheme.spacing.extraSmall)
                .size(14.dp),
        )
    }
}
