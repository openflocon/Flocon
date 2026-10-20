package io.github.openflocon.flocondesktop.features.timeline

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.filter
import flocondesktop.composeapp.generated.resources.timeline_empty
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.network.list.view.components.FilterBar
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineAction
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineItemUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSummaryUiModel
import io.github.openflocon.flocondesktop.features.timeline.view.SourceChip
import io.github.openflocon.flocondesktop.features.timeline.view.TimelineRowView
import io.github.openflocon.flocondesktop.features.timeline.view.label
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconFeature
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TimelineScreen(
    modifier: Modifier = Modifier,
) {
    val viewModel: TimelineViewModel = koinViewModel()
    val rows = viewModel.items.collectAsLazyPagingItems()
    val filterText = viewModel.filterText.collectAsStateWithLifecycle()
    val selectedSources by viewModel.selectedSources.collectAsStateWithLifecycle()
    val summary by viewModel.summary.collectAsStateWithLifecycle()

    TimelineScreen(
        modifier = modifier.fillMaxSize(),
        rows = rows,
        filterText = filterText,
        selectedSources = selectedSources,
        summary = summary,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun TimelineScreen(
    rows: LazyPagingItems<TimelineItemUiModel>,
    filterText: State<String>,
    selectedSources: Set<TimelineSource>,
    summary: TimelineSummaryUiModel,
    onAction: (TimelineAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    FloconFeature(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            FilterBar(
                filterText = filterText,
                placeholderText = stringResource(Res.string.filter),
                modifier = Modifier.weight(1f),
                onTextChange = { onAction(TimelineAction.FilterTextChanged(it)) },
            )
            TimelineSource.entries.forEach { source ->
                SourceChip(
                    source = source,
                    count = summary.sourceCounts[source] ?: 0,
                    selected = source in selectedSources,
                    onClick = { onAction(TimelineAction.ToggleSource(source)) },
                )
            }
        }

        if (rows.itemCount == 0) {
            Text(
                text = stringResource(Res.string.timeline_empty),
                style = FloconTheme.typography.bodyMedium,
                color = FloconTheme.colorPalette.onPrimary,
                modifier = Modifier.padding(FloconTheme.spacing.large),
            )
        }

        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
        ) {
            items(count = rows.itemCount) { index ->
                when (val item = rows[index]) {
                    is TimelineItemUiModel.Row -> TimelineRowView(
                        row = item.row,
                        onClick = { onAction(TimelineAction.Open(it)) },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    is TimelineItemUiModel.DayHeader -> Text(
                        text = item.day,
                        style = FloconTheme.typography.labelMedium,
                        color = FloconTheme.colorPalette.onPrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = FloconTheme.spacing.medium, top = FloconTheme.spacing.medium, bottom = FloconTheme.spacing.extraSmall),
                    )

                    null -> Unit
                }
            }
        }
    }
}
