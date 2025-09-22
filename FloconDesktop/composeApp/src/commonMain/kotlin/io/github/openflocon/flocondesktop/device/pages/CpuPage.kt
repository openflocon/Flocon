package io.github.openflocon.flocondesktop.device.pages

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.device.DeviceAction
import io.github.openflocon.flocondesktop.device.models.CpuUiState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider

@Composable
internal fun CpuPage(
    state: CpuUiState,
    onAction: (DeviceAction) -> Unit
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
                cpuUsage = "Usage (%)",
                userPercentage = "User (%)",
                kernelPercentage = "Kernel (%)",
                packageName = "Package Name",
                pId = "pId",
                majorFaults = "Major Faults",
                minorFaults = "Minor Faults",
                style = FloconTheme.typography.labelSmall
                    .copy(fontWeight = FontWeight.Bold)
            )
            FloconHorizontalDivider(
                color = FloconTheme.colorPalette.secondary
            )
        }
        itemsIndexed(
            items = state.list,
            key = { _, item -> item.pId }
        ) { index, item ->
            BasicItem(
                cpuUsage = item.cpuUsage.toString(),
                userPercentage = item.userPercentage.toString(),
                kernelPercentage = item.kernelPercentage.toString(),
                pId = item.pId.toString(),
                packageName = item.packageName,
                majorFaults = item.majorFaults?.toString().orEmpty(),
                minorFaults = item.minorFaults?.toString().orEmpty(),
                style = FloconTheme.typography.labelSmall
            )
            if (index != state.list.lastIndex) {
                FloconHorizontalDivider(
                    color = FloconTheme.colorPalette.secondary
                )
            }
        }
    }
}

@Composable
private fun BasicItem(
    cpuUsage: String,
    userPercentage: String,
    kernelPercentage: String,
    pId: String,
    packageName: String,
    majorFaults: String,
    minorFaults: String,
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
            text = cpuUsage,
            style = style,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(.2f)
        )
        Text(
            text = userPercentage,
            style = style,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(.2f)
        )
        Text(
            text = kernelPercentage,
            style = style,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(.2f)
        )
        Text(
            text = pId,
            style = style,
            modifier = Modifier.weight(.2f)
        )
        Text(
            text = packageName,
            style = style,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = majorFaults,
            style = style,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(.2f)
        )
        Text(
            text = minorFaults,
            style = style,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(.2f)
        )
    }
}
