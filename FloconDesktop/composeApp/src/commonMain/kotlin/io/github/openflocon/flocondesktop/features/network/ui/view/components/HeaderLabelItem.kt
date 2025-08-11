@file:OptIn(ExperimentalComposeUiApi::class)

package io.github.openflocon.flocondesktop.features.network.ui.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.arrow_up_cropped
import io.github.openflocon.domain.common.ui.interactions.hover
import io.github.openflocon.flocondesktop.features.network.ui.model.SortedByUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.painterResource

private val iconsSize = 8.dp

private const val NOT_ENABLED_OPACITY = 0.3f

@Composable
fun HeaderLabelItem(
    text: String,
    modifier: Modifier = Modifier,
    sortedBy: SortedByUiModel = SortedByUiModel.None, // TODO remove
    isFiltered: Boolean = false, // TODO remove
    clickOnSort: (SortedByUiModel.Enabled) -> Unit,
    clickOnFilter: () -> Unit,
    labelAlignment: Alignment = Alignment.Center,
) {
    var isHover by remember { mutableStateOf(false) }
    val typo = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
    val textColor = FloconTheme.colorPalette.onSurface.copy(alpha = 0.7f)

    Row(
        modifier = modifier
            .hover { isHover = it }
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Image(
            imageVector = Icons.Rounded.FilterAlt,
            contentDescription = "Filter",
            colorFilter = ColorFilter.tint(
                if (isFiltered) textColor else textColor.copy(alpha = NOT_ENABLED_OPACITY),
            ),
            modifier = Modifier.size(14.dp)
                .graphicsLayer {
                    alpha = if (isFiltered || isHover) 1f else 0f
                }
                .clickable(
                    onClick = clickOnFilter,
                ),
        )
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = labelAlignment,
        ) {
            Text(
                text = text,
                style = typo,
                textAlign = TextAlign.Center,
                color = textColor,
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // image to not have paddings
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .graphicsLayer {
                        alpha =
                            if (isHover || sortedBy is SortedByUiModel.Enabled.Ascending) 1f else 0f
                    }
                    .clickable(onClick = {
                        clickOnSort(SortedByUiModel.Enabled.Ascending)
                    }),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Image(
                    painter = painterResource(Res.drawable.arrow_up_cropped),
                    contentDescription = "Sort ascending",
                    colorFilter = ColorFilter.tint(
                        when (sortedBy) {
                            SortedByUiModel.Enabled.Ascending -> textColor
                            SortedByUiModel.Enabled.Descending,
                            SortedByUiModel.None,
                            -> textColor.copy(alpha = NOT_ENABLED_OPACITY)
                        },
                    ),
                    modifier = Modifier.size(iconsSize),
                )
            }
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .graphicsLayer {
                        alpha = if (isHover || sortedBy is SortedByUiModel.Enabled.Descending) 1f else 0f
                    }
                    .clickable(
                        onClick = {
                            clickOnSort(SortedByUiModel.Enabled.Descending)
                        },
                    ),
                contentAlignment = Alignment.TopCenter,
            ) {
                Image(
                    painter = painterResource(Res.drawable.arrow_up_cropped),
                    contentDescription = "Sort descending",
                    colorFilter = ColorFilter.tint(
                        when (sortedBy) {
                            SortedByUiModel.Enabled.Ascending -> textColor.copy(alpha = NOT_ENABLED_OPACITY)
                            SortedByUiModel.Enabled.Descending -> textColor
                            SortedByUiModel.None -> textColor.copy(alpha = NOT_ENABLED_OPACITY)
                        },
                    ),
                    modifier = Modifier.size(iconsSize)
                        .rotate(180f),
                )
            }
        }
    }
}
