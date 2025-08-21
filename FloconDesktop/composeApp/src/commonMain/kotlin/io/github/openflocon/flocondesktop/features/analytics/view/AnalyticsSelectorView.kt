package io.github.openflocon.flocondesktop.features.analytics.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsStateUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.DeviceAnalyticsUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.previewDeviceAnalyticsUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun AnalyticsSelectorView(
    analyticsState: AnalyticsStateUiModel,
    onAnalyticsSelected: (DeviceAnalyticsUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)
    val contentPadding =
        PaddingValues(
            horizontal = 8.dp,
            vertical = 4.dp,
        )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Analytics : ",
            color = FloconTheme.colorPalette.onBackground,
            style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
        )

        Spacer(modifier = Modifier.width(4.dp))

        when (analyticsState) {
            AnalyticsStateUiModel.Loading -> {
                // hide
            }

            AnalyticsStateUiModel.Empty -> {
                Text(
                    modifier =
                        Modifier
                            .background(FloconTheme.colorPalette.onBackground, shape = shape)
                            .padding(contentPadding),
                    text = "No Analytics Found",
                    style = FloconTheme.typography.bodySmall,
                    color = FloconTheme.colorPalette.background,
                )
            }

            is AnalyticsStateUiModel.WithContent -> {
                var expanded by remember { mutableStateOf(false) }

                AnalyticsView(
                    analytics = analyticsState.selected,
                    textColor = FloconTheme.colorPalette.background,
                    modifier =
                        Modifier
                            .clip(shape)
                            .background(FloconTheme.colorPalette.onBackground)
                            .clickable { expanded = true }
                            .padding(contentPadding),
                )

                FloconDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    analyticsState.analytics.forEach { analytics ->
                        DropdownMenuItem(
                            text = {
                                AnalyticsView(
                                    analytics = analytics,
                                    textColor = FloconTheme.colorPalette.onBackground,
                                    modifier = Modifier.padding(all = 4.dp),
                                )
                            },
                            onClick = {
                                onAnalyticsSelected(analytics)
                                expanded = false // Close the dropdown after selection
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsView(
    analytics: DeviceAnalyticsUiModel,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = analytics.name,
        color = textColor,
        style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
    )
}

@Preview
@Composable
private fun AnalyticsViewPreview() {
    FloconTheme {
        AnalyticsView(
            analytics = previewDeviceAnalyticsUiModel(),
            textColor = FloconTheme.colorPalette.background,
        )
    }
}
