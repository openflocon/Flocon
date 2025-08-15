package io.github.openflocon.flocondesktop.features.analytics.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsRowUiModel
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun AnalyticsDetailView(modifier: Modifier = Modifier, state: AnalyticsRowUiModel) {
    val scrollState = rememberScrollState()
    val linesLabelWidth: Dp = 130.dp
    SelectionContainer(
        modifier
            .background(FloconTheme.colorPalette.background)
            .verticalScroll(scrollState)
            .padding(all = 18.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            AnalyticsDetailLineTextView(
                modifier = Modifier.fillMaxWidth(),
                label = "Name",
                value = state.eventName,
                labelWidth = linesLabelWidth,
            )
            AnalyticsDetailLineTextView(
                modifier = Modifier.fillMaxWidth(),
                label = "Time",
                value = state.dateFormatted,
                labelWidth = linesLabelWidth,
            )
            state.properties.forEachIndexed { index, property ->
                AnalyticsDetailLineTextView(
                    modifier = Modifier.fillMaxWidth(),
                    label = property.name,
                    value = property.value,
                    labelWidth = linesLabelWidth,
                )
            }
        }
    }
}

@Composable
fun AnalyticsDetailLineTextView(
    label: String,
    value: String,
    labelWidth: Dp,
    modifier: Modifier = Modifier,
) {
    AnalyticsDetailLineView(
        labelWidth = labelWidth,
        label = label,
        modifier = modifier,
    ) {
        Text(
            text = value,
            style = FloconTheme.typography.bodyMedium, // Body text for the URL
            color = FloconTheme.colorPalette.onBackground, // Primary text color
            modifier = Modifier.weight(1f), // Takes remaining space
        )
    }
}

@Composable
fun AnalyticsDetailLineView(
    label: String,
    labelWidth: Dp,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier.padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = FloconTheme.typography.titleSmall, // Slightly smaller title for details
            color = FloconTheme.colorPalette.onBackground.copy(alpha = 0.7f), // Muted label color
            modifier = Modifier.width(labelWidth).padding(end = 8.dp),
        )
        content()
    }
}
