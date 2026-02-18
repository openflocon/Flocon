package io.github.openflocon.flocondesktop.features.adbcommander.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.adbcommander.model.AdbCommanderAction
import io.github.openflocon.flocondesktop.features.adbcommander.model.FlowUiModel
import io.github.openflocon.flocondesktop.features.adbcommander.model.QuickCommand
import io.github.openflocon.flocondesktop.features.adbcommander.model.SavedCommandUiModel
import io.github.openflocon.flocondesktop.features.adbcommander.model.defaultQuickCommands
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconSection
import org.jetbrains.compose.resources.stringResource
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.adb_commander_saved_commands
import flocondesktop.composeapp.generated.resources.adb_commander_no_saved_commands
import flocondesktop.composeapp.generated.resources.adb_commander_automation_flows
import flocondesktop.composeapp.generated.resources.adb_commander_new_flow
import flocondesktop.composeapp.generated.resources.adb_commander_no_flows
import flocondesktop.composeapp.generated.resources.adb_commander_steps_count

@Composable
fun LibraryContent(
    savedCommands: List<SavedCommandUiModel>,
    flows: List<FlowUiModel>,
    onAction: (AdbCommanderAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val categories = defaultQuickCommands.groupBy { it.category }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(all = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            FloconSection(
                title = stringResource(Res.string.adb_commander_saved_commands),
                initialValue = true,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (savedCommands.isEmpty()) {
                        Text(
                            text = stringResource(Res.string.adb_commander_no_saved_commands),
                            style = FloconTheme.typography.bodySmall,
                            color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.6f),
                            modifier = Modifier.padding(8.dp),
                        )
                    }
                    savedCommands.forEach { command ->
                        SavedCommandItem(
                            command = command,
                            onRun = { onAction(AdbCommanderAction.RunSavedCommand(command.command)) },
                            onDelete = { onAction(AdbCommanderAction.DeleteSavedCommand(command.id)) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
        item {
            FloconSection(
                title = stringResource(Res.string.adb_commander_automation_flows),
                initialValue = true,
                actions = {
                    FloconButton(onClick = { onAction(AdbCommanderAction.ShowFlowEditor(null)) }) {
                        FloconIcon(imageVector = Icons.Outlined.Add)
                        Text(stringResource(Res.string.adb_commander_new_flow))
                    }
                },
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (flows.isEmpty()) {
                        Text(
                            text = stringResource(Res.string.adb_commander_no_flows),
                            style = FloconTheme.typography.bodySmall,
                            color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.6f),
                            modifier = Modifier.padding(8.dp),
                        )
                    }
                    flows.forEach { flow ->
                        FlowItem(
                            flow = flow,
                            onRun = { onAction(AdbCommanderAction.ExecuteFlow(flow.id)) },
                            onEdit = { onAction(AdbCommanderAction.ShowFlowEditor(flow.id)) },
                            onDelete = { onAction(AdbCommanderAction.DeleteFlow(flow.id)) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
        categories.forEach { (category, commands) ->
            item {
                FloconSection(
                    title = category,
                    initialValue = false,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        commands.forEach { cmd ->
                            QuickCommandItem(
                                command = cmd,
                                onRun = { onAction(AdbCommanderAction.RunSavedCommand(cmd.command)) },
                                onSave = { onAction(AdbCommanderAction.SaveQuickCommand(cmd.name, cmd.command)) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickCommandItem(
    command: QuickCommand,
    onRun: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = command.name,
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colorPalette.onPrimary,
            )
            Text(
                text = command.command,
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.5f),
            )
        }
        FloconIconButton(onClick = onRun) {
            FloconIcon(imageVector = Icons.Outlined.PlayArrow)
        }
        FloconIconButton(onClick = onSave) {
            FloconIcon(imageVector = Icons.Outlined.Save)
        }
    }
}

@Composable
private fun SavedCommandItem(
    command: SavedCommandUiModel,
    onRun: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = command.name,
                style = FloconTheme.typography.bodyMedium,
                color = FloconTheme.colorPalette.onPrimary,
            )
            Text(
                text = command.command,
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.7f),
            )
            if (!command.description.isNullOrBlank()) {
                Text(
                    text = command.description,
                    style = FloconTheme.typography.bodySmall,
                    color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.5f),
                )
            }
        }
        FloconIconButton(onClick = onRun) {
            FloconIcon(imageVector = Icons.Outlined.PlayArrow)
        }
        FloconIconButton(onClick = onDelete) {
            FloconIcon(imageVector = Icons.Outlined.Delete)
        }
    }
}

@Composable
private fun FlowItem(
    flow: FlowUiModel,
    onRun: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = flow.name,
                style = FloconTheme.typography.bodyMedium,
                color = FloconTheme.colorPalette.onPrimary,
            )
            Text(
                text = stringResource(Res.string.adb_commander_steps_count, flow.stepsCount),
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.7f),
            )
            if (!flow.description.isNullOrBlank()) {
                Text(
                    text = flow.description,
                    style = FloconTheme.typography.bodySmall,
                    color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.5f),
                )
            }
        }
        FloconIconButton(onClick = onRun) {
            FloconIcon(imageVector = Icons.Outlined.PlayArrow)
        }
        FloconIconButton(onClick = onEdit) {
            FloconIcon(imageVector = Icons.Outlined.Edit)
        }
        FloconIconButton(onClick = onDelete) {
            FloconIcon(imageVector = Icons.Outlined.Delete)
        }
    }
}
