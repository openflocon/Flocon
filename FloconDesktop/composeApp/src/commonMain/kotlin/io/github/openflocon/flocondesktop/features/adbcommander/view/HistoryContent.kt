package io.github.openflocon.flocondesktop.features.adbcommander.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.adbcommander.model.AdbCommanderAction
import io.github.openflocon.flocondesktop.features.adbcommander.model.HistoryEntryUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconTextButton
import org.jetbrains.compose.resources.stringResource
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.adb_commander_clear_all
import flocondesktop.composeapp.generated.resources.adb_commander_no_history

@Composable
fun HistoryContent(
    history: List<HistoryEntryUiModel>,
    onAction: (AdbCommanderAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            FloconTextButton(onClick = { onAction(AdbCommanderAction.ClearHistory) }) {
                FloconIcon(imageVector = Icons.Outlined.DeleteSweep)
                Text(stringResource(Res.string.adb_commander_clear_all))
            }
        }

        if (history.isEmpty()) {
            Text(
                text = stringResource(Res.string.adb_commander_no_history),
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.6f),
                modifier = Modifier.padding(16.dp),
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(all = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.weight(1f),
        ) {
            items(history) { entry ->
                HistoryEntryView(
                    entry = entry,
                    onRerun = { onAction(AdbCommanderAction.RerunCommand(entry.command)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun HistoryEntryView(
    entry: HistoryEntryUiModel,
    onRerun: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .clickable { expanded = !expanded }
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FloconIcon(
                imageVector = if (entry.isSuccess) Icons.Outlined.Check else Icons.Outlined.Close,
                tint = if (entry.isSuccess) {
                    FloconTheme.colorPalette.onPrimary
                } else {
                    FloconTheme.colorPalette.error
                },
            )
            Text(
                text = entry.command,
                style = FloconTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = FloconTheme.colorPalette.onPrimary,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = entry.executedAt,
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.5f),
            )
            FloconIconButton(onClick = onRerun) {
                FloconIcon(imageVector = Icons.Outlined.Replay)
            }
        }
        AnimatedVisibility(visible = expanded) {
            Text(
                text = entry.output,
                style = FloconTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
            )
        }
    }
}
