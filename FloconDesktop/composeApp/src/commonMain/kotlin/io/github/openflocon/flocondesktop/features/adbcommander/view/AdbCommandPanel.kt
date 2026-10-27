package io.github.openflocon.flocondesktop.features.adbcommander.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.adb_commander_no_output
import flocondesktop.composeapp.generated.resources.adb_commander_panel_command
import flocondesktop.composeapp.generated.resources.adb_commander_panel_copy_command
import flocondesktop.composeapp.generated.resources.adb_commander_panel_copy_output
import flocondesktop.composeapp.generated.resources.adb_commander_panel_failure
import flocondesktop.composeapp.generated.resources.adb_commander_panel_output
import flocondesktop.composeapp.generated.resources.adb_commander_panel_success
import flocondesktop.composeapp.generated.resources.menu_item_adbCommander
import io.github.openflocon.flocondesktop.common.ui.detail.DetailChip
import io.github.openflocon.flocondesktop.common.ui.detail.DetailEmptyText
import io.github.openflocon.flocondesktop.common.ui.detail.DetailHeader
import io.github.openflocon.flocondesktop.common.ui.detail.DetailPanel
import io.github.openflocon.flocondesktop.common.ui.detail.DetailSectionCard
import io.github.openflocon.flocondesktop.features.adbcommander.AdbCommandPanelViewModel
import io.github.openflocon.flocondesktop.features.adbcommander.model.AdbCommandPanelUiModel
import io.github.openflocon.flocondesktop.features.adbcommander.model.previewAdbCommandPanelUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AdbCommandPanel(
    id: Long
) {
    val viewModel = koinViewModel<AdbCommandPanelViewModel>(key = id.toString()) {
        parametersOf(id)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    uiState?.let {
        Content(
            uiState = it,
            onCopyCommand = viewModel::onCopyCommand,
            onCopyOutput = viewModel::onCopyOutput,
        )
    }
}

@Composable
private fun Content(
    uiState: AdbCommandPanelUiModel,
    onCopyCommand: () -> Unit,
    onCopyOutput: () -> Unit,
) {
    DetailPanel(
        header = {
            DetailHeader(
                label = stringResource(Res.string.menu_item_adbCommander),
                title = uiState.command,
                chips = {
                    DetailChip(
                        text = stringResource(
                            if (uiState.isSuccess) Res.string.adb_commander_panel_success else Res.string.adb_commander_panel_failure
                        )
                    )
                    DetailChip(text = uiState.executedAt)
                },
            )
        },
    ) {
        DetailSectionCard(
            title = stringResource(Res.string.adb_commander_panel_command),
            badge = null,
            copyContentDescription = stringResource(Res.string.adb_commander_panel_copy_command),
            onCopy = onCopyCommand,
        ) {
            Body(text = uiState.command)
        }
        DetailSectionCard(
            title = stringResource(Res.string.adb_commander_panel_output),
            badge = null,
            copyContentDescription = stringResource(Res.string.adb_commander_panel_copy_output),
            onCopy = onCopyOutput.takeIf { uiState.output.isNotEmpty() },
        ) {
            if (uiState.output.isEmpty()) {
                DetailEmptyText(text = stringResource(Res.string.adb_commander_no_output))
            } else {
                Body(text = uiState.output)
            }
        }
    }
}

@Composable
private fun Body(text: String) {
    SelectionContainer {
        Text(
            text = text,
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colors.ink,
            modifier = Modifier
                .fillMaxWidth()
                .padding(FloconTheme.spacing.medium),
        )
    }
}

@Preview
@Composable
private fun AdbCommandPanelPreview() {
    FloconTheme {
        Content(
            uiState = previewAdbCommandPanelUiModel(),
            onCopyCommand = {},
            onCopyOutput = {},
        )
    }
}
