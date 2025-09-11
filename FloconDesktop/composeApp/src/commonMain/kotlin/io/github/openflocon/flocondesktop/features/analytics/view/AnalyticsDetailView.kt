package io.github.openflocon.flocondesktop.features.analytics.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsRowUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.escape.EscapeHandler

@Composable
fun AnalyticsDetailView(
    modifier: Modifier = Modifier,
    state: AnalyticsRowUiModel,
    closeRequested: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val linesLabelWidth: Dp = 130.dp

    EscapeHandler {
        closeRequested()
        true // consume
    }

    SelectionContainer(
        modifier
            .background(FloconTheme.colorPalette.primary)
            .verticalScroll(scrollState)
            .padding(all = 18.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopEnd
            ) {
                FloconIconButton(
                    imageVector = Icons.Outlined.Close,
                    onClick = { closeRequested() },
                )
            }
            AnalyticsDetailLineTextView(
                modifier = Modifier.fillMaxWidth(),
                label = "Name",
                value = state.eventName,
                labelWidth = linesLabelWidth,
                withDivider = false,
            )
            AnalyticsDetailLineTextView(
                modifier = Modifier.fillMaxWidth(),
                label = "Time",
                value = state.dateFormatted,
                labelWidth = linesLabelWidth,
                withDivider = false,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = FloconTheme.colorPalette.secondary,
                        shape = FloconTheme.shapes.medium
                    )
            ) {
                state.properties.forEachIndexed { index, property ->
                    AnalyticsDetailLineTextView(
                        modifier = Modifier.fillMaxWidth(),
                        label = property.name,
                        value = property.value,
                        labelWidth = linesLabelWidth,
                        withDivider = true,
                    )
                    if (index != state.properties.lastIndex) {
                        FloconHorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            color = FloconTheme.colorPalette.secondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsDetailLineTextView(
    label: String,
    value: String,
    labelWidth: Dp,
    withDivider: Boolean,
    modifier: Modifier = Modifier,
) {
    AnalyticsDetailLineView(
        labelWidth = labelWidth,
        label = label,
        modifier = modifier,
        withDivider = withDivider,
    ) {
        Text(
            text = value,
            style = FloconTheme.typography.bodyMedium,
            color = FloconTheme.colorPalette.onPrimary,
            modifier = Modifier.weight(1f)
                .padding(vertical = 6.dp, horizontal = 8.dp),
        )
    }
}

@Composable
fun AnalyticsDetailLineView(
    label: String,
    labelWidth: Dp,
    modifier: Modifier = Modifier,
    withDivider: Boolean,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier.height(intrinsicSize = IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = FloconTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
            ),
            color = FloconTheme.colorPalette.onPrimary,
            modifier = Modifier
                .padding(vertical = 6.dp, horizontal = 8.dp)
                .width(labelWidth),
        )

        VerticalDivider(
            modifier = Modifier.fillMaxHeight(),
            color = if (withDivider) FloconTheme.colorPalette.secondary else Color.Transparent
        )

        content()
    }
}
