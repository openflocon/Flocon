package io.github.openflocon.flocondesktop.device.pages.memory

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.device_memory_column_pid
import flocondesktop.composeapp.generated.resources.device_memory_column_process
import flocondesktop.composeapp.generated.resources.device_memory_column_usage
import flocondesktop.composeapp.generated.resources.device_memory_empty
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconTableHeaderLabel
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.floconPanel
import io.github.openflocon.library.designsystem.components.floconTableHeader
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private val UsageWidth = 120.dp
private val PidWidth = 90.dp

@Composable
internal fun MemoryPage(
    deviceSerial: String,
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<MemoryViewModel> { parametersOf(deviceSerial) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MemoryPage(
        uiState = uiState,
        modifier = modifier
    )
}

@Composable
private fun MemoryPage(
    uiState: MemoryUiState,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val scrollAdapter = rememberFloconScrollbarAdapter(listState)

    Box(
        modifier = modifier
            .fillMaxSize()
            .floconPanel()
    ) {
        if (uiState.list.isEmpty()) {
            Text(
                text = stringResource(Res.string.device_memory_empty),
                style = FloconTheme.typography.bodyMedium,
                color = FloconTheme.colors.ink.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                MemoryHeader(modifier = Modifier.fillMaxWidth())
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(vertical = FloconTheme.spacing.small)
                ) {
                    itemsIndexed(
                        items = uiState.list,
                        key = { _, item -> item.pid }
                    ) { index, item ->
                        MemoryRow(
                            item = item,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (index != uiState.list.lastIndex) {
                            HorizontalDivider(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
            FloconVerticalScrollbar(
                adapter = scrollAdapter,
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.TopEnd)
            )
        }
    }
}

@Composable
private fun MemoryHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .floconTableHeader()
            .padding(horizontal = FloconTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FloconTableHeaderLabel(
            text = stringResource(Res.string.device_memory_column_process),
            modifier = Modifier
                .weight(1f)
                .padding(end = FloconTheme.spacing.medium)
        )
        FloconTableHeaderLabel(
            text = stringResource(Res.string.device_memory_column_pid),
            textAlign = TextAlign.End,
            modifier = Modifier
                .width(PidWidth)
                .padding(end = FloconTheme.spacing.medium)
        )
        FloconTableHeaderLabel(
            text = stringResource(Res.string.device_memory_column_usage),
            textAlign = TextAlign.End,
            modifier = Modifier.width(UsageWidth)
        )
    }
}

@Composable
private fun MemoryRow(
    item: MemoryItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .heightIn(min = FloconTheme.sizes.rowHeight)
            .padding(horizontal = FloconTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.processName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = FloconTheme.typography.listItem,
            color = FloconTheme.colors.ink,
            modifier = Modifier
                .weight(1f)
                .padding(end = FloconTheme.spacing.medium)
        )
        Text(
            text = item.pid.toString(),
            maxLines = 1,
            textAlign = TextAlign.End,
            style = FloconTheme.typography.listItemMono,
            color = FloconTheme.colors.ink.copy(alpha = 0.5f),
            modifier = Modifier
                .width(PidWidth)
                .padding(end = FloconTheme.spacing.medium)
        )
        Text(
            text = item.memoryUsage,
            maxLines = 1,
            textAlign = TextAlign.End,
            style = FloconTheme.typography.listItemMono,
            color = FloconTheme.colors.ink,
            modifier = Modifier.width(UsageWidth)
        )
    }
}

@Preview
@Composable
private fun MemoryPagePreview() {
    FloconTheme {
        MemoryPage(uiState = previewMemoryUiState())
    }
}

@Preview
@Composable
private fun MemoryPageEmptyPreview() {
    FloconTheme {
        MemoryPage(uiState = MemoryUiState(list = persistentListOf()))
    }
}
