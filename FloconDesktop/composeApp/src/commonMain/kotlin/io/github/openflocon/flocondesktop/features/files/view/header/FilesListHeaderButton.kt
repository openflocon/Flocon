package io.github.openflocon.flocondesktop.features.files.view.header

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ArrowDropUp
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.network.list.model.SortedByUiModel
import io.github.openflocon.library.designsystem.FloconTheme

private const val NOT_ENABLED_OPACITY = 0.3f

@Composable
internal fun FilesListHeaderButton(
    title: String,
    subtitle: String? = null,
    sortedBy: SortedByUiModel,
    clickOnSort: (SortedByUiModel) -> Unit,
    textAlign: TextAlign = TextAlign.Center,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val shape = RoundedCornerShape(4.dp)
    val typo = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
    val textColor = FloconTheme.colorPalette.onSurface.copy(alpha = 0.7f)

    Row(
        modifier = modifier
            .height(FilterIcon_Height)
            .padding(horizontal = 4.dp)
            .hoverable(interactionSource),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FilterIcon_Spacing),
    ) {

        Column(
            modifier = Modifier.weight(1f)
                .clip(shape)
                .background(
                    if (sortedBy != SortedByUiModel.None) FloconTheme.colorPalette.accent.copy(
                        alpha = 0.3f
                    ) else Color.Transparent
                )
                .clickable(onClick = {
                    // TODO
                }),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                textAlign = textAlign,
                color = FloconTheme.colorPalette.onSecondary.copy(alpha = 0.6f),
            )

            subtitle?.let {
                Text(
                    text = it,
                    modifier = Modifier.fillMaxWidth(),
                    style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    textAlign = textAlign,
                    color = FloconTheme.colorPalette.onSecondary.copy(alpha = 0.6f),
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(FilterIcon_Spacing),
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
                },
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
                },
            )
        }
    }
}