package io.github.openflocon.flocondesktop.features.command

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composeunstyled.Text
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconDialog
import io.github.openflocon.library.designsystem.components.FloconDialogButtons
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconTonalButton
import io.github.openflocon.library.designsystem.components.FloconLinearProgressIndicator
import io.github.openflocon.library.designsystem.components.FloconScaffold
import io.github.openflocon.library.designsystem.components.FloconTextField
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AdbCommandScreen() {
    val viewModel = koinViewModel<AdbCommandViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Content(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
private fun Content(
    uiState: AdbUiState,
    onAction: (AdbCommandAction) -> Unit
) {
    var openCreateDialog by remember { mutableStateOf(false) }

    FloconScaffold(
        floatingActionButton = {
            FloconIconTonalButton(
                onClick = { openCreateDialog = true }
            ) {
                FloconIcon(
                    imageVector = Icons.Outlined.Add
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            items(
                items = uiState.commands,
                key = AdbCommandUi::id
            ) { item ->
                Box(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .clip(FloconTheme.shapes.medium)
                        .background(FloconTheme.colorPalette.primary)
                        .padding(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.command,
                            style = FloconTheme.typography.labelLarge,
                            color = FloconTheme.colorPalette.onPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        FloconIconTonalButton(
                            onClick = { onAction(AdbCommandAction.Execute(item)) },
                            containerColor = FloconTheme.colorPalette.secondary
                        ) {
                            FloconIcon(
                                imageVector = Icons.Outlined.PlayCircle
                            )
                        }
                        FloconIconTonalButton(
                            onClick = { onAction(AdbCommandAction.Execute(item)) },
                            containerColor = FloconTheme.colorPalette.secondary
                        ) {
                            FloconIcon(
                                imageVector = Icons.Outlined.PlayCircle
                            )
                        }
                    }
                    if (item.loading) {
                        FloconLinearProgressIndicator(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable(onClick = {}, indication = null, interactionSource = null)
                                .wrapContentSize()
                        )
                    }
                }
            }
        }
    }

    if (openCreateDialog) {
        FloconDialog(
            onDismissRequest = { openCreateDialog = false }
        ) {
            var command by remember { mutableStateOf("") }
            FloconTextField(
                value = command,
                prefix = { Text("adb") },
                onValueChange = { command = it }
            )
            FloconDialogButtons(
                onCancel = { openCreateDialog = false },
                onValidate = {
                    onAction(AdbCommandAction.Create(command))
                    openCreateDialog = false
                }
            )
        }
    }
}
