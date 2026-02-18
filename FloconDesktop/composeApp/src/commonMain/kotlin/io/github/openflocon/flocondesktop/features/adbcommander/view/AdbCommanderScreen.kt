package io.github.openflocon.flocondesktop.features.adbcommander.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.adbcommander.AdbCommanderViewModel
import io.github.openflocon.flocondesktop.features.adbcommander.model.AdbCommanderTab
import io.github.openflocon.flocondesktop.features.adbcommander.model.AdbCommanderUiState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconFeature
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconTextField
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AdbCommanderScreen(modifier: Modifier = Modifier) {
    val viewModel: AdbCommanderViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AdbCommanderScreen(
        uiState = uiState,
        onTabSelected = viewModel::onTabSelected,
        onCommandInputChanged = viewModel::onCommandInputChanged,
        onExecuteCommand = viewModel::onExecuteCommand,
        onSaveCurrentCommand = viewModel::onSaveCurrentCommand,
        onRunSavedCommand = viewModel::onRunSavedCommand,
        onDeleteSavedCommand = viewModel::onDeleteSavedCommand,
        onSaveQuickCommand = viewModel::onSaveQuickCommand,
        onClearHistory = viewModel::onClearHistory,
        onRerunCommand = viewModel::onRerunCommand,
        onClearConsole = viewModel::onClearConsole,
        onShowFlowEditor = viewModel::onShowFlowEditor,
        onDismissFlowEditor = viewModel::onDismissFlowEditor,
        onFlowEditorNameChanged = viewModel::onFlowEditorNameChanged,
        onFlowEditorDescriptionChanged = viewModel::onFlowEditorDescriptionChanged,
        onFlowEditorStepCommandChanged = viewModel::onFlowEditorStepCommandChanged,
        onFlowEditorStepLabelChanged = viewModel::onFlowEditorStepLabelChanged,
        onFlowEditorStepDelayChanged = viewModel::onFlowEditorStepDelayChanged,
        onFlowEditorAddStep = viewModel::onFlowEditorAddStep,
        onFlowEditorRemoveStep = viewModel::onFlowEditorRemoveStep,
        onSaveFlow = viewModel::onSaveFlow,
        onDeleteFlow = viewModel::onDeleteFlow,
        onExecuteFlow = viewModel::onExecuteFlow,
        onCancelFlowExecution = viewModel::onCancelFlowExecution,
        modifier = modifier,
    )
}

@Composable
private fun AdbCommanderScreen(
    uiState: AdbCommanderUiState,
    onTabSelected: (AdbCommanderTab) -> Unit,
    onCommandInputChanged: (String) -> Unit,
    onExecuteCommand: () -> Unit,
    onSaveCurrentCommand: () -> Unit,
    onRunSavedCommand: (String) -> Unit,
    onDeleteSavedCommand: (Long) -> Unit,
    onSaveQuickCommand: (String, String) -> Unit,
    onClearHistory: () -> Unit,
    onRerunCommand: (String) -> Unit,
    onClearConsole: () -> Unit,
    onShowFlowEditor: (Long?) -> Unit,
    onDismissFlowEditor: () -> Unit,
    onFlowEditorNameChanged: (String) -> Unit,
    onFlowEditorDescriptionChanged: (String) -> Unit,
    onFlowEditorStepCommandChanged: (Int, String) -> Unit,
    onFlowEditorStepLabelChanged: (Int, String) -> Unit,
    onFlowEditorStepDelayChanged: (Int, String) -> Unit,
    onFlowEditorAddStep: () -> Unit,
    onFlowEditorRemoveStep: (Int) -> Unit,
    onSaveFlow: () -> Unit,
    onDeleteFlow: (Long) -> Unit,
    onExecuteFlow: (Long) -> Unit,
    onCancelFlowExecution: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloconFeature(modifier = modifier) {
        FloconPageTopBar(
            modifier = Modifier.fillMaxWidth(),
            selector = {
                AdbCommanderTab.entries.forEach { tab ->
                    FloconButton(
                        onClick = { onTabSelected(tab) },
                        containerColor = if (tab == uiState.selectedTab) {
                            FloconTheme.colorPalette.accent
                        } else {
                            FloconTheme.colorPalette.surface
                        },
                    ) {
                        Text(text = tab.name)
                    }
                }
            },
            filterBar = {
                FloconTextField(
                    value = uiState.commandInput,
                    onValueChange = onCommandInputChanged,
                    placeholder = defaultPlaceHolder("Enter ADB command (e.g. shell echo hello)"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onExecuteCommand() }),
                    modifier = Modifier.weight(1f),
                )
            },
            actions = {
                FloconIconButton(onClick = onSaveCurrentCommand) {
                    FloconIcon(imageVector = Icons.Outlined.Save)
                }
                FloconIconButton(onClick = onExecuteCommand) {
                    FloconIcon(imageVector = Icons.AutoMirrored.Outlined.Send)
                }
            },
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(FloconTheme.shapes.medium)
                .background(FloconTheme.colorPalette.primary)
        ) {
            when (uiState.selectedTab) {
                AdbCommanderTab.Runner -> RunnerContent(
                    consoleOutput = uiState.consoleOutput,
                    flowExecution = uiState.flowExecution,
                    isExecuting = uiState.isExecuting,
                    onClearConsole = onClearConsole,
                    onCancelFlowExecution = onCancelFlowExecution,
                    modifier = Modifier.fillMaxSize(),
                )
                AdbCommanderTab.Library -> LibraryContent(
                    savedCommands = uiState.savedCommands,
                    flows = uiState.flows,
                    onRunSavedCommand = onRunSavedCommand,
                    onDeleteSavedCommand = onDeleteSavedCommand,
                    onShowFlowEditor = onShowFlowEditor,
                    onDeleteFlow = onDeleteFlow,
                    onExecuteFlow = onExecuteFlow,
                    onSaveQuickCommand = onSaveQuickCommand,
                    modifier = Modifier.fillMaxSize(),
                )
                AdbCommanderTab.History -> HistoryContent(
                    history = uiState.history,
                    onClearHistory = onClearHistory,
                    onRerunCommand = onRerunCommand,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }

    if (uiState.showFlowEditor) {
        FlowEditorDialog(
            state = uiState.flowEditorState,
            onDismiss = onDismissFlowEditor,
            onNameChanged = onFlowEditorNameChanged,
            onDescriptionChanged = onFlowEditorDescriptionChanged,
            onStepCommandChanged = onFlowEditorStepCommandChanged,
            onStepLabelChanged = onFlowEditorStepLabelChanged,
            onStepDelayChanged = onFlowEditorStepDelayChanged,
            onAddStep = onFlowEditorAddStep,
            onRemoveStep = onFlowEditorRemoveStep,
            onSave = onSaveFlow,
        )
    }
}
