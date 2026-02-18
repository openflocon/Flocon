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

@Composable
fun FlowEditorDialog(
    state: FlowEditorState,
    onDismiss: () -> Unit,
    onNameChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onStepCommandChanged: (Int, String) -> Unit,
    onStepLabelChanged: (Int, String) -> Unit,
    onStepDelayChanged: (Int, String) -> Unit,
    onAddStep: () -> Unit,
    onRemoveStep: (Int) -> Unit,
    onSave: () -> Unit,
) {
    FloconDialog(onDismissRequest = onDismiss) {
        Column {
            FloconDialogHeader(
                title = if (state.flowId != null) "Edit Flow" else "New Flow",
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
                    onValueChange = onNameChanged,
                    placeholder = defaultPlaceHolder("Flow name"),
                    modifier = Modifier.fillMaxWidth(),
                )

                FloconTextField(
                    value = state.description,
                    onValueChange = onDescriptionChanged,
                    placeholder = defaultPlaceHolder("Description (optional)"),
                    modifier = Modifier.fillMaxWidth(),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Steps",
                        style = FloconTheme.typography.titleSmall,
                        color = FloconTheme.colorPalette.onPrimary,
                    )
                    FloconButton(onClick = onAddStep) {
                        FloconIcon(imageVector = Icons.Outlined.Add)
                        Text("Add Step")
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
                            onCommandChanged = { onStepCommandChanged(index, it) },
                            onLabelChanged = { onStepLabelChanged(index, it) },
                            onDelayChanged = { onStepDelayChanged(index, it) },
                            onRemove = { onRemoveStep(index) },
                            canRemove = state.steps.size > 1,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                FloconDialogButtons(
                    onCancel = onDismiss,
                    onValidate = onSave,
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
                placeholder = defaultPlaceHolder("ADB command"),
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                FloconTextField(
                    value = label,
                    onValueChange = onLabelChanged,
                    placeholder = defaultPlaceHolder("Label (optional)"),
                    modifier = Modifier.weight(1f),
                )
                FloconTextField(
                    value = delayAfterMs,
                    onValueChange = onDelayChanged,
                    placeholder = defaultPlaceHolder("Delay (ms)"),
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
