package io.github.openflocon.flocondesktop.features.adbcommander.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.adbcommander.AdbCommanderViewModel
import io.github.openflocon.flocondesktop.features.adbcommander.model.AdbCommanderAction
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
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.adb_commander_input_placeholder

@Composable
fun AdbCommanderScreen(modifier: Modifier = Modifier) {
    val viewModel: AdbCommanderViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AdbCommanderScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
private fun AdbCommanderScreen(
    uiState: AdbCommanderUiState,
    onAction: (AdbCommanderAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    FloconFeature(modifier = modifier) {
        FloconPageTopBar(
            modifier = Modifier.fillMaxWidth(),
            selector = {
                AdbCommanderTab.entries.forEach { tab ->
                    FloconButton(
                        onClick = { onAction(AdbCommanderAction.TabSelected(tab)) },
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
                    onValueChange = { onAction(AdbCommanderAction.CommandInputChanged(it)) },
                    placeholder = defaultPlaceHolder(stringResource(Res.string.adb_commander_input_placeholder)),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onAction(AdbCommanderAction.ExecuteCommand) }),
                    modifier = Modifier.weight(1f),
                )
            },
            actions = {
                FloconIconButton(onClick = { onAction(AdbCommanderAction.SaveCurrentCommand) }) {
                    FloconIcon(imageVector = Icons.Outlined.Save)
                }
                FloconIconButton(onClick = { onAction(AdbCommanderAction.ExecuteCommand) }) {
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
                    onAction = onAction,
                    modifier = Modifier.fillMaxSize(),
                )
                AdbCommanderTab.Library -> LibraryContent(
                    savedCommands = uiState.savedCommands,
                    flows = uiState.flows,
                    onAction = onAction,
                    modifier = Modifier.fillMaxSize(),
                )
                AdbCommanderTab.History -> HistoryContent(
                    history = uiState.history,
                    onAction = onAction,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }

    if (uiState.showFlowEditor) {
        FlowEditorDialog(
            state = uiState.flowEditorState,
            onAction = onAction,
        )
    }
}
