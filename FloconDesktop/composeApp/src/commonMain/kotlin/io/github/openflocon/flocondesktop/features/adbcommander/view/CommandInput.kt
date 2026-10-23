package io.github.openflocon.flocondesktop.features.adbcommander.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.adb_commander_execute
import flocondesktop.composeapp.generated.resources.adb_commander_input_placeholder
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDocButton
import io.github.openflocon.flocondesktop.features.adbcommander.model.AdbCommanderAction
import io.github.openflocon.flocondesktop.features.adbcommander.model.HistoryEntryUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconExposedDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconExposedDropdownMenuBox
import io.github.openflocon.library.designsystem.components.FloconTextField
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommandInput(
    commandInput: String,
    history: ImmutableList<HistoryEntryUiModel>,
    onAction: (AdbCommanderAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isInputEmpty = commandInput.isBlank()

    Column(
        modifier = modifier
            .background(
                color = FloconTheme.colors.bg100,
                shape = FloconTheme.shapes.medium
            )
    ) {
        // Toolbar row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(horizontal = FloconTheme.spacing.medium)
        ) {
            // Save icon
            Box(
                modifier = Modifier.clip(RoundedCornerShape(2.dp))
                    .clickable(enabled = isInputEmpty.not()) {
                        onAction(AdbCommanderAction.SaveCurrentCommand)
                    }.aspectRatio(1f, true),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    Icons.Filled.StarBorder,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                        .graphicsLayer(
                            alpha = if (isInputEmpty) 0.6f else 1f
                        ),
                    colorFilter = ColorFilter.tint(FloconTheme.colors.ink)
                )
            }

            VerticalDivider(modifier = Modifier.padding(vertical = FloconTheme.spacing.compact, horizontal = FloconTheme.spacing.tiny))

            // Copy icon
            Box(
                modifier = Modifier.clip(RoundedCornerShape(2.dp))
                    .clickable(enabled = isInputEmpty.not()) {
                        onAction(AdbCommanderAction.CopyCommand)
                    }.aspectRatio(1f, true),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    Icons.Filled.CopyAll,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                        .graphicsLayer(
                            alpha = if (isInputEmpty) 0.6f else 1f
                        ),
                    colorFilter = ColorFilter.tint(FloconTheme.colors.ink)
                )
            }

            VerticalDivider(modifier = Modifier.padding(vertical = FloconTheme.spacing.compact, horizontal = FloconTheme.spacing.tiny))

            // History dropdown
            var isHistoryExpanded by remember { mutableStateOf(false) }
            val historyEntries = history.take(20)
            val displayHistory = isHistoryExpanded && historyEntries.isNotEmpty()

            FloconExposedDropdownMenuBox(
                expanded = displayHistory,
                onExpandedChange = { isHistoryExpanded = false },
            ) {
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(2.dp))
                        .clickable(enabled = historyEntries.isNotEmpty()) {
                            isHistoryExpanded = true
                        }.aspectRatio(1f, true),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        Icons.Outlined.History,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        colorFilter = ColorFilter.tint(FloconTheme.colors.ink)
                    )
                }

                FloconExposedDropdownMenu(
                    expanded = displayHistory,
                    onDismissRequest = { isHistoryExpanded = false },
                    modifier = Modifier.width(300.dp)
                ) {
                    historyEntries.fastForEach { entry ->
                        Text(
                            modifier = Modifier.padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small).clickable {
                                onAction(AdbCommanderAction.RerunCommand(entry.command))
                                isHistoryExpanded = false
                            },
                            text = entry.command,
                            style = FloconTheme.typography.bodySmall,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            FeatureDocButton(
                doc = FeatureDoc.AdbCommander,
                modifier = Modifier.align(Alignment.CenterVertically),
            )

            // Clear/Delete icon
            Box(
                modifier = Modifier.clip(RoundedCornerShape(2.dp)).clickable {
                    onAction(AdbCommanderAction.ClearCommand)
                }.aspectRatio(1f, true),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    Icons.Filled.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(FloconTheme.colors.ink)
                )
            }
        }

        // Multi-line command input
        FloconTextField(
            value = commandInput,
            onValueChange = { onAction(AdbCommanderAction.CommandInputChanged(it)) },
            singleLine = false,
            minLines = 3,
            maxLines = 6,
            textStyle = FloconTheme.typography.bodyMedium,
            containerColor = FloconTheme.colors.bg200,
            placeholder = defaultPlaceHolder(stringResource(Res.string.adb_commander_input_placeholder)),
            modifier = Modifier.fillMaxWidth()
                .onKeyEvent { keyEvent ->
                    if (keyEvent.type == KeyEventType.KeyDown &&
                        keyEvent.key == androidx.compose.ui.input.key.Key.Enter &&
                        (keyEvent.isMetaPressed || keyEvent.isCtrlPressed)
                    ) {
                        onAction(AdbCommanderAction.ExecuteCommand)
                        return@onKeyEvent true
                    }
                    return@onKeyEvent false
                }.padding(horizontal = FloconTheme.spacing.compact, vertical = FloconTheme.spacing.extraSmall),
        )

        // Execute button row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FloconButton(
                onClick = {
                    if (!isInputEmpty) {
                        onAction(AdbCommanderAction.ExecuteCommand)
                    }
                },
                containerColor = FloconTheme.colors.ink,
                modifier = Modifier
                    .padding(all = FloconTheme.spacing.small)
                    .graphicsLayer {
                        if (isInputEmpty) alpha = 0.6f
                    }
            ) {
                val contentColor = FloconTheme.colors.bg100
                Row(
                    Modifier.padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.extraSmall),
                    horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        Icons.Filled.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        colorFilter = ColorFilter.tint(contentColor)
                    )
                    Text(stringResource(Res.string.adb_commander_execute), color = contentColor)
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
