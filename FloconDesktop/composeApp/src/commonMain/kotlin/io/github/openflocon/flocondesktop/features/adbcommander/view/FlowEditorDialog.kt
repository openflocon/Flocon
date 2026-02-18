package io.github.openflocon.flocondesktop.features.adbcommander.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.adbcommander.model.AdbCommanderAction
import io.github.openflocon.flocondesktop.features.adbcommander.model.FlowEditorState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDialog
import io.github.openflocon.library.designsystem.components.FloconDialogButtons
import io.github.openflocon.library.designsystem.components.FloconDialogHeader
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconTextField
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder
import org.jetbrains.compose.resources.stringResource
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.adb_commander_edit_flow
import flocondesktop.composeapp.generated.resources.adb_commander_new_flow
import flocondesktop.composeapp.generated.resources.adb_commander_flow_name
import flocondesktop.composeapp.generated.resources.adb_commander_flow_description
import flocondesktop.composeapp.generated.resources.adb_commander_steps
import flocondesktop.composeapp.generated.resources.adb_commander_add_step
import flocondesktop.composeapp.generated.resources.adb_commander_step_command
import flocondesktop.composeapp.generated.resources.adb_commander_step_label
import flocondesktop.composeapp.generated.resources.adb_commander_step_delay

@Composable
fun FlowEditorDialog(
    state: FlowEditorState,
    onAction: (AdbCommanderAction) -> Unit,
) {
    FloconDialog(onDismissRequest = { onAction(AdbCommanderAction.DismissFlowEditor) }) {
        Column {
            FloconDialogHeader(
                title = if (state.flowId != null) {
                    stringResource(Res.string.adb_commander_edit_flow)
                } else {
                    stringResource(Res.string.adb_commander_new_flow)
                },
                modifier = Modifier.fillMaxWidth(),
            )

            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FloconTextField(
                    value = state.name,
                    onValueChange = { onAction(AdbCommanderAction.FlowEditorNameChanged(it)) },
                    placeholder = defaultPlaceHolder(stringResource(Res.string.adb_commander_flow_name)),
                    modifier = Modifier.fillMaxWidth(),
                )

                FloconTextField(
                    value = state.description,
                    onValueChange = { onAction(AdbCommanderAction.FlowEditorDescriptionChanged(it)) },
                    placeholder = defaultPlaceHolder(stringResource(Res.string.adb_commander_flow_description)),
                    modifier = Modifier.fillMaxWidth(),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(Res.string.adb_commander_steps),
                        style = FloconTheme.typography.titleSmall,
                        color = FloconTheme.colorPalette.onPrimary,
                    )
                    FloconButton(onClick = { onAction(AdbCommanderAction.FlowEditorAddStep) }) {
                        FloconIcon(imageVector = Icons.Outlined.Add)
                        Text(stringResource(Res.string.adb_commander_add_step))
                    }
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    itemsIndexed(state.steps) { index, step ->
                        StepEditorItem(
                            index = index,
                            command = step.command,
                            label = step.label,
                            delayAfterMs = step.delayAfterMs,
                            onCommandChanged = { onAction(AdbCommanderAction.FlowEditorStepCommandChanged(index, it)) },
                            onLabelChanged = { onAction(AdbCommanderAction.FlowEditorStepLabelChanged(index, it)) },
                            onDelayChanged = { onAction(AdbCommanderAction.FlowEditorStepDelayChanged(index, it)) },
                            onRemove = { onAction(AdbCommanderAction.FlowEditorRemoveStep(index)) },
                            canRemove = state.steps.size > 1,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                FloconDialogButtons(
                    onCancel = { onAction(AdbCommanderAction.DismissFlowEditor) },
                    onValidate = { onAction(AdbCommanderAction.SaveFlow) },
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun StepEditorItem(
    index: Int,
    command: String,
    label: String,
    delayAfterMs: String,
    onCommandChanged: (String) -> Unit,
    onLabelChanged: (String) -> Unit,
    onDelayChanged: (String) -> Unit,
    onRemove: () -> Unit,
    canRemove: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = "${index + 1}.",
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colorPalette.onPrimary,
            modifier = Modifier.padding(top = 8.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            FloconTextField(
                value = command,
                onValueChange = onCommandChanged,
                placeholder = defaultPlaceHolder(stringResource(Res.string.adb_commander_step_command)),
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                FloconTextField(
                    value = label,
                    onValueChange = onLabelChanged,
                    placeholder = defaultPlaceHolder(stringResource(Res.string.adb_commander_step_label)),
                    modifier = Modifier.weight(1f),
                )
                FloconTextField(
                    value = delayAfterMs,
                    onValueChange = onDelayChanged,
                    placeholder = defaultPlaceHolder(stringResource(Res.string.adb_commander_step_delay)),
                    modifier = Modifier.weight(0.5f),
                )
            }
        }
        if (canRemove) {
            FloconIconButton(onClick = onRemove) {
                FloconIcon(imageVector = Icons.Outlined.Delete)
            }
        }
    }
}
