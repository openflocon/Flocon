package io.github.openflocon.flocondesktop.features.dashboard.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.general_auto
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardArrangement
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource

private const val MaxItemsPerRow = 4

/** Segmented control: Auto, then a fixed number of items per row. */
@Composable
internal fun DashboardArrangementContent(
    arrangement: DashboardArrangement,
    onArrangementClicked: (DashboardArrangement) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors

    Row(
        modifier = modifier
            .height(FloconTheme.sizes.pageBarControlHeight)
            .clip(FloconTheme.shapes.medium)
            .background(colors.ink.copy(alpha = 0.06f))
            .padding(FloconTheme.spacing.tiny),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.tiny),
    ) {
        ArrangementSegment(
            text = stringResource(Res.string.general_auto),
            isSelected = arrangement is DashboardArrangement.Adaptive,
            onClick = { onArrangementClicked(DashboardArrangement.Adaptive) },
        )
        repeat(MaxItemsPerRow) { index ->
            val itemsPerRow = index + 1
            ArrangementSegment(
                text = itemsPerRow.toString(),
                isSelected = (arrangement as? DashboardArrangement.Fixed)?.itemsPerRow == itemsPerRow,
                onClick = { onArrangementClicked(DashboardArrangement.Fixed(itemsPerRow)) },
            )
        }
    }
}

@Composable
private fun ArrangementSegment(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val shape = FloconTheme.shapes.small

    Text(
        modifier = modifier
            .clip(shape)
            .background(if (isSelected) colors.ink.copy(alpha = 0.14f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small),
        text = text,
        style = FloconTheme.typography.bodySmall,
        fontWeight = FontWeight.Medium,
        color = if (isSelected) colors.ink else colors.inkMuted,
        maxLines = 1,
    )
}

@Preview
@Composable
private fun DashboardArrangementContentPreview() {
    FloconTheme {
        DashboardArrangementContent(
            arrangement = DashboardArrangement.Adaptive,
            onArrangementClicked = {},
        )
    }
}
