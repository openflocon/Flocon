package io.github.openflocon.flocondesktop.features.files.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.with_folders_size
import io.github.openflocon.flocondesktop.features.files.model.FilePathUiModel
import io.github.openflocon.flocondesktop.features.files.model.FileTypeUiModel
import io.github.openflocon.flocondesktop.features.files.model.FileUiModel
import io.github.openflocon.flocondesktop.features.files.model.FilesStateUiModel
import io.github.openflocon.flocondesktop.features.files.model.previewFilesStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconIconToggleButton
import io.github.openflocon.library.designsystem.components.FloconIconTonalButton
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FilesTopBar(
    current: FileUiModel?,
    numberOfFiles: Int?,
    onBack: () -> Unit,
    onDeleteContent: () -> Unit,
    onRefresh: () -> Unit,
    updateWithFoldersSize: (Boolean) -> Unit,
    filterBar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    options: FilesStateUiModel.Options,
    canSelect: Boolean,
    selecting: Boolean,
    selectedCount: Int,
    onDeleteSelection: () -> Unit,
    onMultiSelect: () -> Unit,
    onClearMultiSelect: () -> Unit,
) {
    val hasParentFile = current != null
    val isDirectory = current?.isDirectory == true

    FloconPageTopBar(
        modifier = modifier,
        filterBar = {
            FloconIconTonalButton(
                onClick = onBack,
                enabled = hasParentFile,
                containerColor = FloconTheme.colorPalette.secondary
            ) {
                FloconIcon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack
                )
            }

            Text(
                text = current?.name.orEmpty(),
                style = FloconTheme.typography.bodyMedium,
                maxLines = 1,
                color = FloconTheme.colorPalette.onPrimary,
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 8.dp),
            )

            numberOfFiles?.let { nb ->
                Text(
                    text = "$nb files",
                    style = FloconTheme.typography.bodySmall,
                    maxLines = 1,
                    color = FloconTheme.colorPalette.onSecondary,
                    modifier = Modifier
                        .background(FloconTheme.colorPalette.secondary.copy(alpha = 0.6f), shape = FloconTheme.shapes.medium)
                        .padding(vertical = 3.dp, horizontal = 8.dp),
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            AnimatedVisibility(canSelect) {
                SelectingView(
                    selecting = selecting,
                    selectedCount = selectedCount,
                    onDeleteSelection = onDeleteSelection,
                    onMultiSelect = onMultiSelect,
                    onClearMultiSelect = onClearMultiSelect,
                )
            }

            filterBar()
        },
        actions = {
            FloconIconToggleButton(
                value = options.withFoldersSize,
                onValueChange = updateWithFoldersSize,
                tooltip = stringResource(Res.string.with_folders_size),
            ) {
                FloconIcon(
                    imageVector = Icons.Outlined.RemoveRedEye
                )
            }

            // Delete folder content button (hidden in selection mode)
            FloconIconButton(
                imageVector = Icons.Outlined.Delete,
                enabled = isDirectory,
                onClick = onDeleteContent,
            )

            FloconIconButton(
                imageVector = Icons.Outlined.Refresh,
                enabled = isDirectory,
                onClick = onRefresh,
            )
        }
    )
}

@Composable
private fun SelectingView(
    selecting: Boolean,
    selectedCount: Int,
    modifier: Modifier = Modifier,
    onDeleteSelection: () -> Unit,
    onClearMultiSelect: () -> Unit,
    onMultiSelect: () -> Unit,
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Multi-select toggle button
        AnimatedVisibility(selecting && selectedCount > 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.background(
                    color = FloconTheme.colorPalette.accent,
                    shape = FloconTheme.shapes.medium,
                ).padding(
                    horizontal = 8.dp
                )
            ) {
                Text(
                    text = "$selectedCount selected",
                    style = FloconTheme.typography.bodyMedium,
                    color = FloconTheme.colorPalette.onAccent,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
                FloconIconButton(
                    imageVector = Icons.Outlined.Delete,
                    enabled = true,
                    onClick = onDeleteSelection,
                )
            }
        }

        FloconIconButton(
            imageVector = if (selecting) Icons.Outlined.Close else Icons.Outlined.CheckBox,
            onClick = if (selecting) onClearMultiSelect else onMultiSelect,
            tooltip = if (selecting) "Delete Selection" else "Select multiple files"
        )
    }
}

@Preview
@Composable
private fun FilesTopBarPreview_noParent() {
    FloconTheme {
        FilesTopBar(
            current = null,
            onBack = {},
            onDeleteContent = {},
            onRefresh = {},
            filterBar = {},
            modifier = Modifier.fillMaxWidth(),
            updateWithFoldersSize = {},
            options = previewFilesStateUiModel().options,
            onDeleteSelection = {},
            onClearMultiSelect = {},
            onMultiSelect = {},
            canSelect = true,
            selecting = false,
            selectedCount = 0,
            numberOfFiles = null,
        )
    }
}

@Preview
@Composable
private fun FilesTopBarPreview() {
    val current = FileUiModel(
        name = "File Name",
        type = FileTypeUiModel.Folder,
        path = FilePathUiModel.Constants.CachesDir,
        icon = Icons.Outlined.Folder,
        sizeFormatted = "10 KB",
        contextualActions = emptyList(),
        dateFormatted = "2022-01-01 12:10",
    )
    FloconTheme {
        FilesTopBar(
            current = current,
            onBack = {},
            onDeleteContent = {},
            onRefresh = {},
            filterBar = {
                Button(onClick = {}) {
                    Text(text = "Filter")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            updateWithFoldersSize = {},
            options = previewFilesStateUiModel().options,
            onDeleteSelection = {},
            onClearMultiSelect = {},
            onMultiSelect = {},
            canSelect = true,
            selecting = false,
            selectedCount = 0,
            numberOfFiles = null,
        )
    }
}
