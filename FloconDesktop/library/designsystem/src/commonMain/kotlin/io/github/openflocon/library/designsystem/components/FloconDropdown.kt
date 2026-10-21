@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.library.designsystem.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.ImageVector
import com.composeunstyled.DropdownMenuPanel
import com.composeunstyled.DropdownPanelAnchor
import io.github.openflocon.library.designsystem.FloconTheme

internal val DropdownEnterTransition = fadeIn(tween(durationMillis = 180))
    .plus(
        scaleIn(
            initialScale = 0.85f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    )
    .plus(slideInVertically(tween(durationMillis = 220)) { -it / 8 })

internal val DropdownExitTransition = fadeOut(tween(durationMillis = 120))
    .plus(
        scaleOut(
            targetScale = 0.9f,
            animationSpec = tween(durationMillis = 120),
        )
    )
    .plus(slideOutVertically(tween(durationMillis = 120)) { -it / 10 })

@Composable
fun FloconDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = FloconTheme.colors.bg200,
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
        DropdownMenuPanel(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            shape = FloconTheme.shapes.large,
            backgroundColor = FloconTheme.colors.bg100,
            contentColor = FloconTheme.colors.ink,
            contentPadding = PaddingValues(FloconTheme.spacing.compact),
            anchor = anchor,
            enter = DropdownEnterTransition,
            exit = DropdownExitTransition,
            modifier = Modifier
                .padding(top = FloconTheme.spacing.small)
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
    modifier: Modifier = Modifier,
    secondaryAction: (@Composable () -> Unit)? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
) {
    FloconMenuItem(
        text = text,
        onClick = onClick,
        modifier = modifier,
        secondaryAction = secondaryAction,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
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
