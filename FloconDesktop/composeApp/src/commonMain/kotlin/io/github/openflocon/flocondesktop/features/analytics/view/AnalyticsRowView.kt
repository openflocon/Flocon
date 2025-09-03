package io.github.openflocon.flocondesktop.features.analytics.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import io.github.openflocon.flocondesktop.common.ui.ContextualItem
import io.github.openflocon.flocondesktop.common.ui.ContextualView
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsAction
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsRowUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.previewAnalyticsRowUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AnalyticsRowView(
    model: AnalyticsRowUiModel,
    onAction: (action: AnalyticsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    ContextualView(
        items = contextualActions(
            onAction = onAction,
            state = model,
        ),
    ) {
        Column(
            modifier = modifier
                .clip(shape = RoundedCornerShape(8.dp))
                .background(color = FloconTheme.colorPalette.primary)
                .clickable(onClick = {
                    onAction(AnalyticsAction.OnClick(model))
                })
                .padding(horizontal = 12.dp)
                .padding(top = 8.dp, bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = model.eventName,
                style = FloconTheme.typography.titleSmall,
                color = FloconTheme.colorPalette.onPrimary,
            )

            Text(
                text = model.dateFormatted,
                style = FloconTheme.typography.titleSmall.copy(fontWeight = FontWeight.Light),
                color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.4f),
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                model.properties.fastForEach {
                    Row(
                        Modifier
                            .background(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White.copy(alpha = 0.1f),
                            )
                            .padding(horizontal = 12.dp, vertical = 1.dp),
                    ) {
                        Text(
                            text = it.name + " :",
                            style = FloconTheme.typography.titleSmall.copy(fontWeight = FontWeight.Light),
                            color = FloconTheme.colorPalette.onPrimary,
                        )
                        Text(
                            text = it.value,
                            style = FloconTheme.typography.titleSmall,
                            color = FloconTheme.colorPalette.onPrimary,
                        )
                    }
                }
                if (model.hasMoreProperties) {
                    Text(
                        text = "...",
                        modifier =
                            Modifier
                                .padding(horizontal = 4.dp),
                        style = FloconTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = FloconTheme.colorPalette.onPrimary,
                    )
                }
            }
        }
    }
}


@Composable
private fun contextualActions(
    onAction: (AnalyticsAction) -> Unit,
    state: AnalyticsRowUiModel
): List<ContextualItem> {
    val onActionCallback by rememberUpdatedState(onAction)
    return remember(state) {
        buildList {
            add(
                ContextualItem(
                    text = "Remove",
                    onClick = {
                        onActionCallback(AnalyticsAction.Remove(state))
                    }
                ),
            )
            add(
                ContextualItem(
                    text = "Remove lines above ",
                    onClick = {
                        onActionCallback(AnalyticsAction.RemoveLinesAbove(state))
                    }
                ),
            )
        }
    }
}


@Preview
@Composable
private fun SharedPreferenceRowPreview() {
    FloconTheme {
        AnalyticsRowView(
            modifier = Modifier.fillMaxWidth(),
            model = previewAnalyticsRowUiModel(),
            onAction = {},
        )
    }
}
