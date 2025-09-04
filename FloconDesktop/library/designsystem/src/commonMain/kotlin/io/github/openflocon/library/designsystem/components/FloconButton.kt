package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.theme.contentColorFor

private val VerticalContentPadding = 4.dp
private val HorizontalContentPadding = 8.dp

@Composable
fun FloconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = FloconTheme.colorPalette.primary,
    content: @Composable RowScope.() -> Unit
) {
    FloconSurface(
        color = containerColor,
        contentColor = FloconTheme.colorPalette.contentColorFor(containerColor),
        onClick = onClick,
        modifier = modifier,
        shape = FloconTheme.shapes.medium
    ) {
        ProvideTextStyle(FloconTheme.typography.labelLarge) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(PaddingValues(vertical = VerticalContentPadding, horizontal = HorizontalContentPadding)),
                content = content
            )
        }
    }
}

@Composable
fun FloconOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    FloconSurface(
        color = Color.Transparent,
        contentColor = FloconTheme.colorPalette.onSurface,
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, FloconTheme.colorPalette.onSurface),
    ) {
        CompositionLocalProvider(LocalTextStyle provides FloconTheme.typography.labelLarge) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(PaddingValues(vertical = VerticalContentPadding, horizontal = HorizontalContentPadding)),
                content = content
            )
        }
    }
}
