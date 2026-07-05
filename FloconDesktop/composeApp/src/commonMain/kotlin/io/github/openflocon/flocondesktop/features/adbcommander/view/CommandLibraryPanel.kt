package io.github.openflocon.flocondesktop.features.adbcommander.view

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.adb_commander_automation_flows
import flocondesktop.composeapp.generated.resources.adb_commander_new_flow
import flocondesktop.composeapp.generated.resources.adb_commander_no_flows
import flocondesktop.composeapp.generated.resources.adb_commander_no_saved_commands
import flocondesktop.composeapp.generated.resources.adb_commander_quick_commands
import flocondesktop.composeapp.generated.resources.adb_commander_saved_commands
import flocondesktop.composeapp.generated.resources.adb_commander_steps_count
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

@Composable
fun CommandLibraryPanel(
    savedCommands: List<SavedCommandUiModel>,
    flows: List<FlowUiModel>,
    onAction: (AdbCommanderAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = FloconTheme.colorPalette.secondary
    val categories = defaultQuickCommands.groupBy { it.category }

    Surface(
        color = FloconTheme.colorPalette.primary,
        modifier = modifier
            .clip(FloconTheme.shapes.medium)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = FloconTheme.shapes.medium
            )
    ) {
        Column(
            Modifier.fillMaxSize()
        ) {
            // Top zone: Saved Commands + Automation Flows
            Column(
                Modifier.fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(all = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
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
                            CommandRow(
                                name = command.name,
                                commandText = command.command,
                                onClick = { onAction(AdbCommanderAction.RunSavedCommand(command.command)) },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                FloconIconButton(onClick = { onAction(AdbCommanderAction.DeleteSavedCommand(command.id)) }) {
                                    FloconIcon(imageVector = Icons.Outlined.Delete)
                                }
                            }
                        }
                    }
                }

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
                            CommandRow(
                                name = flow.name,
                                commandText = stringResource(Res.string.adb_commander_steps_count, flow.stepsCount),
                                onClick = { onAction(AdbCommanderAction.ExecuteFlow(flow.id)) },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                FloconIconButton(onClick = { onAction(AdbCommanderAction.ShowFlowEditor(flow.id)) }) {
                                    FloconIcon(imageVector = Icons.Outlined.Edit)
                                }
                                FloconIconButton(onClick = { onAction(AdbCommanderAction.DeleteFlow(flow.id)) }) {
                                    FloconIcon(imageVector = Icons.Outlined.Delete)
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = borderColor)

            // Bottom zone: Quick Commands
            Column(
                Modifier.fillMaxWidth()
                    .weight(0.4f)
                    .verticalScroll(rememberScrollState())
                    .padding(all = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    stringResource(Res.string.adb_commander_quick_commands),
                    color = FloconTheme.colorPalette.onSurface,
                    style = FloconTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )

                categories.forEach { (category, commands) ->
                    FloconSection(
                        title = category,
                        initialValue = false,
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            commands.forEach { cmd ->
                                CommandRow(
                                    name = cmd.name,
                                    commandText = cmd.command,
                                    onClick = { onAction(AdbCommanderAction.RunSavedCommand(cmd.command)) },
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    FloconIconButton(onClick = { onAction(AdbCommanderAction.SaveQuickCommand(cmd.name, cmd.command)) }) {
                                        FloconIcon(imageVector = Icons.Outlined.Save)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CommandRow(
    name: String,
    commandText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colorPalette.onPrimary,
            )
            Text(
                text = commandText,
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.5f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        actions()
    }
}
