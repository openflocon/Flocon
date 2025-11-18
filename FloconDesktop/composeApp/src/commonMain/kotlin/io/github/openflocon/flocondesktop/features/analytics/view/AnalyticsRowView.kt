package io.github.openflocon.flocondesktop.features.analytics.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import io.github.openflocon.flocondesktop.common.ui.ContextualView
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsAction
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsRowUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.previewAnalyticsRowUiModel
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkAction
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.common.FloconContextMenuItem
import androidx.compose.ui.tooling.preview.Preview

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
        Row(
            modifier = modifier
                .height(IntrinsicSize.Min)
                .clickable(onClick = { onAction(AnalyticsAction.OnClick(model)) })
                .padding(vertical = 8.dp)
                .then(
                    if (model.isFromOldAppInstance) {
                        Modifier.alpha(0.4f)
                    } else Modifier
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = model.dateFormatted,
                modifier = Modifier.width(110.dp),
                textAlign = TextAlign.Center,
                style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Light),
                color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.4f),
            )

            Text(
                text = model.eventName,
                modifier = Modifier.width(200.dp),
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colorPalette.onPrimary,
            )

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                model.properties.fastForEach {
                    Row(
                        Modifier
                            .background(
                                shape = FloconTheme.shapes.extraLarge,
                                color = FloconTheme.colorPalette.secondary
                            )
                            .padding(horizontal = 8.dp, vertical = 1.dp),
                    ) {
                        Text(
                            text = it.name + " :",
                            style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Light),
                            color = FloconTheme.colorPalette.onSecondary,
                        )
                        Text(
                            text = it.value,
                            style = FloconTheme.typography.bodySmall,
                            color = FloconTheme.colorPalette.onSecondary,
                        )
                    }
                }
                if (model.hasMoreProperties) {
                    Text(
                        text = "...",
                        modifier =
                            Modifier
                                .padding(horizontal = 4.dp),
                        style = FloconTheme.typography.bodySmall,
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
): List<FloconContextMenuItem> {
    val onActionCallback by rememberUpdatedState(onAction)
    return remember(state) {
        buildList {
            add(
                FloconContextMenuItem.Item(
                    label = "Remove",
                    onClick = {
                        onActionCallback(AnalyticsAction.Remove(state))
                    }
                ),
            )
            add(
                FloconContextMenuItem.Item(
                    label = "Remove lines above ",
                    onClick = {
                        onActionCallback(AnalyticsAction.RemoveLinesAbove(state))
                    }
                ),
            )
            add(
                FloconContextMenuItem.Item(
                    label =  "Clear old sessions",
                    onClick = {
                        onActionCallback(AnalyticsAction.ClearOldSession)
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
