package io.github.openflocon.flocondesktop.features.adbcommander.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.adb_commander_unavailable_wifi
import io.github.openflocon.flocondesktop.features.adbcommander.AdbCommanderViewModel
import io.github.openflocon.flocondesktop.features.adbcommander.model.AdbCommanderAction
import io.github.openflocon.flocondesktop.features.adbcommander.model.AdbCommanderUiState
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource
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
    if (!uiState.adbAvailable) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(Res.string.adb_commander_unavailable_wifi),
                style = FloconTheme.typography.bodyMedium,
                color = FloconTheme.colors.ink,
            )
        }
        return
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        Row(Modifier.fillMaxSize()) {
            CommandLibraryPanel(
                savedCommands = uiState.savedCommands,
                flows = uiState.flows,
                onAction = onAction,
                modifier = Modifier.fillMaxHeight().width(340.dp),
            )

            Spacer(modifier = Modifier.width(FloconTheme.spacing.medium))

            Column(modifier = Modifier.fillMaxWidth()) {
                CommandInput(
                    commandInput = uiState.commandInput,
                    history = uiState.history,
                    onAction = onAction,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(FloconTheme.spacing.small))

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
