package io.github.openflocon.flocondesktop.features.command

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composeunstyled.Text
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconDialog
import io.github.openflocon.library.designsystem.components.FloconDialogButtons
import io.github.openflocon.library.designsystem.components.FloconFeature
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconTonalButton
import io.github.openflocon.library.designsystem.components.FloconLinearProgressIndicator
import io.github.openflocon.library.designsystem.components.FloconTextField
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AdbCommandScreen(
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<AdbCommandViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DisposableEffect(viewModel) {
        viewModel.onVisible()
        onDispose { viewModel.onNotVisible() }
    }

    Content(
        uiState = uiState,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

@Composable
private fun Content(
        uiState: AdbUiState,
        onAction: (AdbCommandAction) -> Unit,
        modifier: Modifier = Modifier
) {
    var openCreateDialog by remember { mutableStateOf(false) }

    FloconFeature(modifier = modifier.fillMaxSize()) {
        Row(Modifier.fillMaxSize()) {
            AdbSavedCommandsView(
                    modifier = Modifier.fillMaxHeight().width(340.dp),
                    commands = uiState.commands,
                    onAction = onAction,
                    onAddClicked = { openCreateDialog = true }
            )
            Spacer(modifier = Modifier.width(12.dp))

            AdbHistoryView(
                    modifier = Modifier.fillMaxWidth(),
                    history = uiState.history,
                    onAction = onAction
            )
        }
    }

    if (openCreateDialog) {
        AdbCreateDialog(
                onDismiss = { openCreateDialog = false },
                onCreate = { command ->
                    onAction(AdbCommandAction.Create(command))
                    openCreateDialog = false
                }
        )
    }
}

@Composable
private fun AdbSavedCommandsView(
        modifier: Modifier = Modifier,
        commands: List<AdbCommandUi>,
        onAction: (AdbCommandAction) -> Unit,
        onAddClicked: () -> Unit
) {
    val borderColor = FloconTheme.colorPalette.secondary

    Surface(
            color = FloconTheme.colorPalette.primary,
            modifier =
                    modifier.clip(FloconTheme.shapes.medium)
                            .border(
                                    width = 1.dp,
                                    color = borderColor,
                                    shape = FloconTheme.shapes.medium
                            )
    ) {
        Column(Modifier.fillMaxSize()) {
            Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                        "Saved Commands",
                        color = FloconTheme.colorPalette.onSurface,
                        style =
                                FloconTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold
                                ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                )
                FloconIconTonalButton(
                        onClick = onAddClicked,
                        containerColor = FloconTheme.colorPalette.secondary
                ) { FloconIcon(imageVector = Icons.Outlined.Add) }
            }
            LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier.fillMaxSize()
            ) {
                items(items = commands, key = { it.id }) { item ->
                    AdbCommandItemView(item = item, onAction = onAction, showDelete = true)
                }
            }
        }
    }
}

@Composable
private fun AdbHistoryView(
        modifier: Modifier = Modifier,
        history: List<AdbCommandUi>,
        onAction: (AdbCommandAction) -> Unit
) {
    val lazyListState = rememberLazyListState()
    val scrollAdapter = rememberFloconScrollbarAdapter(lazyListState)

    Box(
            modifier =
                    modifier.fillMaxSize()
                            .clip(FloconTheme.shapes.medium)
                            .background(FloconTheme.colorPalette.primary)
                            .border(
                                    width = 1.dp,
                                    color = FloconTheme.colorPalette.secondary,
                                    shape = FloconTheme.shapes.medium
                            )
    ) {
        Column(Modifier.fillMaxSize()) {
            Text(
                    "History",
                    color = FloconTheme.colorPalette.onSurface,
                    style = FloconTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(16.dp)
            )
            LazyColumn(
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize()
            ) {
                items(history) { item ->
                    AdbCommandItemView(item = item, onAction = onAction, showDelete = false)
                }
            }
        }
        FloconVerticalScrollbar(
                adapter = scrollAdapter,
                modifier = Modifier.fillMaxHeight().align(Alignment.TopEnd)
        )
    }
}

@Composable
private fun AdbCommandItemView(
        item: AdbCommandUi,
        onAction: (AdbCommandAction) -> Unit,
        showDelete: Boolean = false
) {
    Box(
            modifier =
                    Modifier.fillMaxWidth()
                            .clip(FloconTheme.shapes.medium)
                            .background(FloconTheme.colorPalette.secondary.copy(alpha = 0.5f))
                            .padding(8.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                        text = "adb ${item.command}",
                        style = FloconTheme.typography.labelLarge,
                        color = FloconTheme.colorPalette.onPrimary,
                        modifier = Modifier.weight(1f)
                )
                if (showDelete) {
                    FloconIconTonalButton(
                            onClick = { onAction(AdbCommandAction.Delete(item)) },
                            containerColor = FloconTheme.colorPalette.primary
                    ) { FloconIcon(imageVector = Icons.Default.Delete) }
                }
                FloconIconTonalButton(
                        onClick = { onAction(AdbCommandAction.Execute(item)) },
                        containerColor = FloconTheme.colorPalette.primary
                ) { FloconIcon(imageVector = Icons.Outlined.PlayCircle) }
            }
            if (item.output != null) {
                Text(
                        text = item.output,
                        style = FloconTheme.typography.bodySmall,
                        color = FloconTheme.colorPalette.onSecondary,
                        modifier =
                                Modifier.fillMaxWidth()
                                        .background(
                                                FloconTheme.colorPalette.primary.copy(alpha = 0.3f)
                                        )
                                        .padding(4.dp)
                )
            }
        }
        if (item.loading) {
            FloconLinearProgressIndicator(
                    modifier =
                            Modifier.matchParentSize()
                                    .clickable(
                                            onClick = {},
                                            indication = null,
                                            interactionSource = null
                                    )
                                    .wrapContentSize()
            )
        }
    }
}

@Composable
private fun AdbCreateDialog(onDismiss: () -> Unit, onCreate: (String) -> Unit) {
    FloconDialog(onDismissRequest = onDismiss) {
        var command by remember { mutableStateOf("") }
        FloconTextField(
                value = command,
                prefix = { Text("adb ") },
                onValueChange = { command = it }
        )
        FloconDialogButtons(onCancel = onDismiss, onValidate = { onCreate(command) })
    }
}
