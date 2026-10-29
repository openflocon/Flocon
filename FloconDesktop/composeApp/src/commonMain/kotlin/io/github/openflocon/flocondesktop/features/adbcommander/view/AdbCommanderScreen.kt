package io.github.openflocon.flocondesktop.features.adbcommander.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.adb_commander_unavailable_wifi
import flocondesktop.composeapp.generated.resources.menu_item_adbCommander
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureEmptyState
import io.github.openflocon.flocondesktop.features.adbcommander.AdbCommanderViewModel
import io.github.openflocon.flocondesktop.features.adbcommander.model.AdbCommanderAction
import io.github.openflocon.flocondesktop.features.adbcommander.model.AdbCommanderUiState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.navigation.mascot.mascotPlatform
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
        FeatureEmptyState(
            doc = FeatureDoc.AdbCommander,
            title = stringResource(Res.string.menu_item_adbCommander),
            description = stringResource(Res.string.adb_commander_unavailable_wifi),
            showSetupButton = false,
            modifier = modifier
                .fillMaxSize()
                .clip(FloconTheme.shapes.medium)
                .background(FloconTheme.colors.bg100)
                .mascotPlatform(),
        )
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
