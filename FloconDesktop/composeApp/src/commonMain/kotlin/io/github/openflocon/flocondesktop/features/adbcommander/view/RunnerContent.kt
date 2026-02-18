package io.github.openflocon.flocondesktop.features.adbcommander.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.RemoveCircle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.adbcommander.model.AdbCommanderAction
import io.github.openflocon.flocondesktop.features.adbcommander.model.ConsoleOutputEntry
import io.github.openflocon.flocondesktop.features.adbcommander.model.FlowExecutionUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.adb_commander_cancel_flow
import io.github.openflocon.library.designsystem.components.FloconCircularProgressIndicator
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconTextButton

@Composable
fun RunnerContent(
    consoleOutput: List<ConsoleOutputEntry>,
    flowExecution: FlowExecutionUiModel?,
    isExecuting: Boolean,
    onAction: (AdbCommanderAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(consoleOutput.size) {
        if (consoleOutput.isNotEmpty()) {
            listState.animateScrollToItem(consoleOutput.size - 1)
        }
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isExecuting) {
                FloconCircularProgressIndicator()
            }
            if (flowExecution?.isRunning == true) {
                FloconTextButton(onClick = { onAction(AdbCommanderAction.CancelFlowExecution) }) {
                    FloconIcon(imageVector = Icons.Outlined.Cancel)
                    Text(stringResource(Res.string.adb_commander_cancel_flow))
                }
            }
            FloconIconButton(onClick = { onAction(AdbCommanderAction.ClearConsole) }) {
                FloconIcon(imageVector = Icons.Outlined.DeleteSweep)
            }
        }

        // Flow execution progress
        if (flowExecution != null) {
            FlowExecutionView(
                execution = flowExecution,
                modifier = Modifier.fillMaxWidth().padding(8.dp),
            )
        }

        // Console output
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(all = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f),
        ) {
            items(consoleOutput) { entry ->
                ConsoleEntryView(entry = entry, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun ConsoleEntryView(
    entry: ConsoleOutputEntry,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = "$ ${entry.command}",
            style = FloconTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
            color = FloconTheme.colorPalette.accent,
        )
        Text(
            text = entry.output,
            style = FloconTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
            color = if (entry.isSuccess) {
                FloconTheme.colorPalette.onPrimary
            } else {
                FloconTheme.colorPalette.error
            },
        )
    }
}

@Composable
private fun FlowExecutionView(
    execution: FlowExecutionUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "Flow: ${execution.flowName} - ${execution.status}",
            style = FloconTheme.typography.titleSmall,
            color = FloconTheme.colorPalette.onPrimary,
        )
        execution.steps.forEach { step ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FloconIcon(
                    imageVector = step.status.toIcon(),
                    tint = step.status.toColor(),
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = step.label,
                    style = FloconTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = step.status.toColor(),
                )
                if (step.isActive) {
                    FloconCircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun String.toIcon(): ImageVector = when (this) {
    "Completed" -> Icons.Outlined.Check
    "Failed" -> Icons.Outlined.Close
    "Running" -> Icons.Outlined.PlayArrow
    "WaitingDelay" -> Icons.Outlined.HourglassEmpty
    "Skipped" -> Icons.Outlined.RemoveCircle
    else -> Icons.Outlined.Schedule
}

@Composable
private fun String.toColor(): Color = when (this) {
    "Completed" -> FloconTheme.colorPalette.onPrimary
    "Failed" -> FloconTheme.colorPalette.error
    "Running", "WaitingDelay" -> FloconTheme.colorPalette.accent
    else -> FloconTheme.colorPalette.onPrimary.copy(alpha = 0.5f)
}
