package io.github.openflocon.flocondesktop.features.analytics.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
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
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.analytics_column_event
import flocondesktop.composeapp.generated.resources.analytics_column_properties
import flocondesktop.composeapp.generated.resources.clear_old_sessions
import flocondesktop.composeapp.generated.resources.date
import flocondesktop.composeapp.generated.resources.remove
import flocondesktop.composeapp.generated.resources.remove_lines_above
import io.github.openflocon.flocondesktop.common.ui.ContextualMenu
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsAction
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsRowUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.previewAnalyticsRowUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.common.FloconContextMenuItem
import io.github.openflocon.library.designsystem.components.FloconTableHeaderLabel
import io.github.openflocon.library.designsystem.components.floconTableHeader
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

@Composable
fun AnalyticsRow(
    model: AnalyticsRowUiModel,
    onAction: (action: AnalyticsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    ContextualMenu(
        items = contextualActions(
            onAction = onAction,
            state = model,
        ),
    ) {
        Row(
            modifier = modifier
                .height(IntrinsicSize.Min)
                .clickable(onClick = { onAction(AnalyticsAction.OnClick(model)) })
                .padding(vertical = FloconTheme.spacing.small)
                .then(
                    if (model.isFromOldAppInstance) {
                        Modifier.alpha(0.4f)
                    } else {
                        Modifier
                    }
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = model.dateFormatted,
                modifier = Modifier.width(110.dp),
                textAlign = TextAlign.Center,
                style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Light),
                color = FloconTheme.colors.ink.copy(alpha = 0.4f),
            )

            Text(
                text = model.eventName,
                modifier = Modifier.width(200.dp),
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colors.ink,
            )

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall)) {
                model.properties.fastForEach {
                    Row(
                        Modifier
                            .background(
                                shape = FloconTheme.shapes.extraLarge,
                                color = FloconTheme.colors.bg200
                            )
                            .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.tiny),
                    ) {
                        Text(
                            text = it.name + " :",
                            style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Light),
                            color = FloconTheme.colors.inkMuted,
                        )
                        Text(
                            text = it.value,
                            style = FloconTheme.typography.bodySmall,
                            color = FloconTheme.colors.inkMuted,
                        )
                    }
                }
                if (model.hasMoreProperties) {
                    Text(
                        text = "...",
                        modifier =
                        Modifier
                            .padding(horizontal = FloconTheme.spacing.extraSmall),
                        style = FloconTheme.typography.bodySmall,
                        color = FloconTheme.colors.ink,
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
): ImmutableList<FloconContextMenuItem> {
    val onActionCallback by rememberUpdatedState(onAction)
    val removeString = stringResource(Res.string.remove)
    val removeLinesAboveString = stringResource(Res.string.remove_lines_above)
    val clearOldSessionsString = stringResource(Res.string.clear_old_sessions)
    return remember(state) {
        persistentListOf(
            FloconContextMenuItem.Item(
                label = removeString,
                onClick = {
                    onActionCallback(AnalyticsAction.Remove(state))
                }
            ),
            FloconContextMenuItem.Item(
                label = removeLinesAboveString,
                onClick = {
                    onActionCallback(AnalyticsAction.RemoveLinesAbove(state))
                }
            ),
            FloconContextMenuItem.Item(
                label = clearOldSessionsString,
                onClick = {
                    onActionCallback(AnalyticsAction.ClearOldSession)
                }
            ),
        )
    }
}

@Preview
@Composable
private fun SharedPreferenceRowPreview() {
    FloconTheme {
        AnalyticsRow(
            modifier = Modifier.fillMaxWidth(),
            model = previewAnalyticsRowUiModel(),
            onAction = {},
        )
    }
}

@Composable
fun AnalyticsHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.floconTableHeader(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FloconTableHeaderLabel(
            text = stringResource(Res.string.date),
            modifier = Modifier.width(110.dp),
            textAlign = TextAlign.Center,
        )
        FloconTableHeaderLabel(
            text = stringResource(Res.string.analytics_column_event),
            modifier = Modifier.width(200.dp),
        )
        FloconTableHeaderLabel(
            text = stringResource(Res.string.analytics_column_properties),
        )
    }
}
