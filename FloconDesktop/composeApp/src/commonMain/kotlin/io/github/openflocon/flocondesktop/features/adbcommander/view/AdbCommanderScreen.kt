package io.github.openflocon.flocondesktop.features.adbcommander.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.adbcommander.AdbCommanderViewModel
import io.github.openflocon.flocondesktop.features.adbcommander.model.AdbCommanderAction
import io.github.openflocon.flocondesktop.features.adbcommander.model.AdbCommanderUiState
import io.github.openflocon.library.designsystem.components.FloconFeature
import org.koin.compose.viewmodel.koinViewModel

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
        Row(Modifier.fillMaxSize()) {
            CommandLibraryPanel(
                savedCommands = uiState.savedCommands,
                flows = uiState.flows,
                onAction = onAction,
                modifier = Modifier.fillMaxHeight().width(340.dp),
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                CommandInputView(
                    commandInput = uiState.commandInput,
                    history = uiState.history,
                    onAction = onAction,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                RunnerContent(
                    consoleOutput = uiState.consoleOutput,
                    flowExecution = uiState.flowExecution,
                    isExecuting = uiState.isExecuting,
                    onAction = onAction,
                    modifier = Modifier.fillMaxWidth().weight(1f),
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
