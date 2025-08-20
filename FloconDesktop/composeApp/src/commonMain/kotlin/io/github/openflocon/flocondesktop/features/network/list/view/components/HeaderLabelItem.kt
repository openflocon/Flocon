@file:OptIn(ExperimentalComposeUiApi::class)

package io.github.openflocon.flocondesktop.features.network.list.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.network.list.model.SortedByUiModel
import io.github.openflocon.library.designsystem.FloconTheme

private val Height = 32.dp
private val Spacing = 2.dp
private val FilterIcon = (Height - Spacing) / 2

private const val NOT_ENABLED_OPACITY = 0.3f

@Composable
fun HeaderLabelItem(
    text: String,
    modifier: Modifier = Modifier,
    sortedBy: SortedByUiModel = SortedByUiModel.None, // TODO remove
    filtered: Boolean = false, // TODO remove
    clickOnSort: (SortedByUiModel.Enabled) -> Unit,
    clickOnFilter: () -> Unit,
    labelAlignment: Alignment = Alignment.Center,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val shape = RoundedCornerShape(4.dp)
    val typo = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
    val textColor = FloconTheme.colorPalette.onSurface.copy(alpha = 0.7f)

    Row(
        modifier = modifier
            .height(Height)
            .padding(horizontal = 4.dp)
            .hoverable(interactionSource),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing),
    ) {
        Box(
            contentAlignment = labelAlignment,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(shape)
                .background(if (filtered) FloconTheme.colorPalette.surfaceVariant else Color.Transparent)
                .clickable(onClick = clickOnFilter)
        ) {
            Text(
                text = text,
                style = typo,
                textAlign = TextAlign.Center,
                color = textColor,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing),
        ) {
            FilterIcon(
                icon = Icons.Outlined.ArrowDropUp,
                hovered = hovered,
                sorted = sortedBy is SortedByUiModel.Enabled.Ascending,
                onClick = { clickOnSort(SortedByUiModel.Enabled.Ascending) },
                shape = shape,
                alignment = Alignment.BottomCenter,
                textColor = when (sortedBy) {
                    SortedByUiModel.Enabled.Ascending -> textColor
                    SortedByUiModel.Enabled.Descending,
                    SortedByUiModel.None,
                        -> textColor.copy(alpha = NOT_ENABLED_OPACITY)
                }
            )
            FilterIcon(
                icon = Icons.Outlined.ArrowDropDown,
                hovered = hovered,
                sorted = sortedBy is SortedByUiModel.Enabled.Descending,
                onClick = { clickOnSort(SortedByUiModel.Enabled.Descending) },
                shape = shape,
                alignment = Alignment.TopCenter,
                textColor = when (sortedBy) {
                    SortedByUiModel.Enabled.Ascending -> textColor.copy(alpha = NOT_ENABLED_OPACITY)
                    SortedByUiModel.Enabled.Descending -> textColor
                    SortedByUiModel.None -> textColor.copy(alpha = NOT_ENABLED_OPACITY)
                }
            )
        }
    }
}

@Composable
private fun FilterIcon(
    icon: ImageVector,
    hovered: Boolean,
    sorted: Boolean,
    onClick: () -> Unit,
    shape: Shape,
    textColor: Color,
    alignment: Alignment,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(FilterIcon)
            .graphicsLayer {
                alpha = if (hovered || sorted) 1f else 0f
            }
            .clip(shape)
            .clickable(onClick = onClick),
        contentAlignment = alignment,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(FilterIcon)
        )
    }
}
