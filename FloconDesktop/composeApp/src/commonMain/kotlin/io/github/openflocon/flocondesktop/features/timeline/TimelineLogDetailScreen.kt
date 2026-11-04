package io.github.openflocon.flocondesktop.features.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.timeline_log_detail_copy_params
import flocondesktop.composeapp.generated.resources.timeline_log_detail_copy_value
import flocondesktop.composeapp.generated.resources.timeline_log_detail_details
import flocondesktop.composeapp.generated.resources.timeline_log_detail_level
import flocondesktop.composeapp.generated.resources.timeline_log_detail_no_params
import flocondesktop.composeapp.generated.resources.timeline_log_detail_params
import flocondesktop.composeapp.generated.resources.timeline_log_detail_time
import flocondesktop.composeapp.generated.resources.timeline_log_detail_type
import flocondesktop.composeapp.generated.resources.timeline_log_detail_value
import flocondesktop.composeapp.generated.resources.timeline_source_log
import io.github.openflocon.domain.timelinelog.models.TimelineLogLevel
import io.github.openflocon.flocondesktop.common.ui.detail.DetailEmptyText
import io.github.openflocon.flocondesktop.common.ui.detail.DetailHeader
import io.github.openflocon.flocondesktop.common.ui.detail.DetailKeyValueList
import io.github.openflocon.flocondesktop.common.ui.detail.DetailPanel
import io.github.openflocon.flocondesktop.common.ui.detail.DetailSectionCard
import io.github.openflocon.flocondesktop.features.timeline.mapper.color
import io.github.openflocon.flocondesktop.features.timeline.mapper.logGlyphOf
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineLogDetailUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.previewTimelineLogDetailUiModel
import io.github.openflocon.flocondesktop.features.timeline.view.LogGlyphIcon
import io.github.openflocon.flocondesktop.features.timeline.view.label
import io.github.openflocon.library.designsystem.FloconTheme
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun TimelineLogDetailScreen(
    id: Long,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<TimelineLogDetailViewModel>(key = id.toString()) {
        parametersOf(id)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    uiState?.let {
        TimelineLogDetailScreen(
            uiState = it,
            onCopyValue = viewModel::onCopyValue,
            onCopyParams = viewModel::onCopyParams,
            modifier = modifier,
        )
    }
}

@Composable
internal fun TimelineLogDetailScreen(
    uiState: TimelineLogDetailUiModel,
    onCopyValue: () -> Unit,
    onCopyParams: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // the same glyph as the row of the list and the marker of the graph
    val glyph = remember(uiState.type, uiState.level) { logGlyphOf(uiState.type, uiState.level) }
    DetailPanel(
        modifier = modifier,
        header = {
            DetailHeader(
                label = stringResource(Res.string.timeline_source_log),
                title = uiState.value,
                icon = { LogGlyphIcon(glyph = glyph, size = HeaderGlyphSize) },
            )
        },
    ) {
        DetailSectionCard(
            title = stringResource(Res.string.timeline_log_detail_value),
            badge = null,
            copyContentDescription = stringResource(Res.string.timeline_log_detail_copy_value),
            onCopy = onCopyValue,
        ) {
            SelectionContainer {
                Text(
                    text = uiState.value,
                    style = FloconTheme.typography.bodySmall,
                    color = FloconTheme.colors.ink,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(FloconTheme.spacing.medium),
                )
            }
        }
        DetailSectionCard(
            title = stringResource(Res.string.timeline_log_detail_details),
            badge = null,
            copyContentDescription = "",
            onCopy = null,
        ) {
            // the level of a tagged log, in its color; the reserved types carry no level on the wire
            if (!glyph.isTyped) {
                // the level, in its color: the same as the icon in the list and the marker in the graph
                val levelColor = uiState.level.color(FloconTheme.colors)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
                    modifier = Modifier.padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small),
                ) {
                    Text(
                        text = stringResource(Res.string.timeline_log_detail_level),
                        style = FloconTheme.typography.bodySmall,
                        color = FloconTheme.colors.inkMuted,
                    )
                    Box(Modifier.size(LevelDotSize).background(levelColor, CircleShape))
                    Text(
                        text = stringResource(uiState.level.label()),
                        style = FloconTheme.typography.bodySmall,
                        color = levelColor,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            DetailKeyValueList(
                entries = listOf(
                    stringResource(Res.string.timeline_log_detail_type) to uiState.type,
                    stringResource(Res.string.timeline_log_detail_time) to uiState.time,
                ),
            )
        }
        DetailSectionCard(
            title = stringResource(Res.string.timeline_log_detail_params),
            badge = uiState.params.size.takeIf { it > 0 }?.toString(),
            copyContentDescription = stringResource(Res.string.timeline_log_detail_copy_params),
            onCopy = onCopyParams.takeIf { uiState.params.isNotEmpty() },
        ) {
            if (uiState.params.isEmpty()) {
                DetailEmptyText(stringResource(Res.string.timeline_log_detail_no_params))
            } else {
                DetailKeyValueList(entries = uiState.params)
            }
        }
    }
}

@Preview
@Composable
private fun TimelineLogDetailScreenPreview() {
    FloconTheme {
        TimelineLogDetailScreen(
            uiState = previewTimelineLogDetailUiModel(),
            onCopyValue = {},
            onCopyParams = {},
        )
    }
}

@Preview
@Composable
private fun TimelineLogDetailScreenNoParamsPreview() {
    FloconTheme {
        TimelineLogDetailScreen(
            uiState = previewTimelineLogDetailUiModel().copy(params = persistentListOf()),
            onCopyValue = {},
            onCopyParams = {},
        )
    }
}

@Preview
@Composable
private fun TimelineLogDetailScreenTypedPreview() {
    FloconTheme {
        TimelineLogDetailScreen(
            uiState = previewTimelineLogDetailUiModel().copy(value = "HomeScreen", type = "navigation", level = TimelineLogLevel.Info),
            onCopyValue = {},
            onCopyParams = {},
        )
    }
}

private val LevelDotSize = 8.dp
private val HeaderGlyphSize = 20.dp
