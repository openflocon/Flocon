package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.timeline.model.previewTimelineRows
import io.github.openflocon.library.designsystem.FloconTheme

// every kind of event: the first line is hovered, the second one is selected, "users" is the text of the filter
@Composable
private fun TimelineRowsPreviewContent(isDarkTheme: Boolean) {
    FloconTheme(isDarkTheme = isDarkTheme) {
        val rows = previewTimelineRows()
        Column(
            modifier = Modifier
                .background(FloconTheme.colors.bg100)
                .padding(FloconTheme.spacing.medium),
        ) {
            rows.forEachIndexed { index, row ->
                if (row.source == TimelineSource.AppStart) {
                    SessionHeader(row = row, eventCount = 11)
                } else {
                    TimelineRow(
                        row = row,
                        delta = "+12ms",
                        selected = index == 1,
                        hovered = index == 0,
                        highlight = "users",
                        onClick = {},
                        onHoverChange = {},
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun TimelineRowsDarkPreview() = TimelineRowsPreviewContent(isDarkTheme = true)

@Preview
@Composable
private fun TimelineRowsLightPreview() = TimelineRowsPreviewContent(isDarkTheme = false)
