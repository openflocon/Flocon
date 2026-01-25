package io.github.openflocon.flocondesktop.device.pages.memory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun MemoryPage(
    deviceSerial: String
) {
    val viewModel = koinViewModel<MemoryViewModel> { parametersOf(deviceSerial) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Content(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
private fun Content(
    uiState: MemoryUiState,
    onAction: (MemoryAction) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .clip(FloconTheme.shapes.medium)
            .background(FloconTheme.colorPalette.primary)
            .border(
                width = 1.dp,
                color = FloconTheme.colorPalette.secondary,
                shape = FloconTheme.shapes.medium
            )
    ) {
        stickyHeader {
            BasicItem(
                memoryUsage = "Usage",
                processName = "Package name",
                pId = "pId",
                style = FloconTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
        itemsIndexed(
            items = uiState.list,
            key = { _, item -> item.pid }
        ) { index, item ->
            BasicItem(
                memoryUsage = item.memoryUsage,
                pId = item.pid.toString(),
                processName = item.processName,
                style = FloconTheme.typography.labelSmall
            )
            if (index != uiState.list.lastIndex) {
                FloconHorizontalDivider(
                    color = FloconTheme.colorPalette.secondary
                )
            }
        }
    }
}

@Composable
private fun BasicItem(
    memoryUsage: String,
    processName: String,
    pId: String,
    style: TextStyle
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(FloconTheme.colorPalette.primary)
            .padding(4.dp)
    ) {
        Text(
            text = memoryUsage,
            style = style,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(.2f)
        )
        Text(
            text = pId,
            style = style,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(.2f)
        )
        Text(
            text = processName,
            style = style,
            modifier = Modifier.weight(1f)
        )
    }
}
