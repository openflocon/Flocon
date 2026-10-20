package io.github.openflocon.flocondesktop.features.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.filter
import flocondesktop.composeapp.generated.resources.timeline_empty
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.network.list.view.components.FilterBar
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineAction
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRowUiModel
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
    val rows = viewModel.rows.collectAsLazyPagingItems()
    val filterText = viewModel.filterText.collectAsStateWithLifecycle()
    val selectedSources by viewModel.selectedSources.collectAsStateWithLifecycle()

    TimelineScreen(
        modifier = modifier.fillMaxSize(),
        rows = rows,
        filterText = filterText,
        selectedSources = selectedSources,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun TimelineScreen(
    rows: LazyPagingItems<TimelineRowUiModel>,
    filterText: State<String>,
    selectedSources: Set<TimelineSource>,
    onAction: (TimelineAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    FloconFeature(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                modifier = Modifier.padding(16.dp),
            )
        }

        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(count = rows.itemCount) { index ->
                rows[index]?.let { row ->
                    TimelineRowView(
                        row = row,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun SourceChip(
    source: TimelineSource,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(source.label()),
        style = FloconTheme.typography.labelMedium,
        color = if (selected) FloconTheme.colorPalette.onAccent else FloconTheme.colorPalette.onSecondary,
        maxLines = 1,
        modifier = modifier
            .clip(FloconTheme.shapes.medium)
            .background(if (selected) FloconTheme.colorPalette.accent else FloconTheme.colorPalette.secondary)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}
