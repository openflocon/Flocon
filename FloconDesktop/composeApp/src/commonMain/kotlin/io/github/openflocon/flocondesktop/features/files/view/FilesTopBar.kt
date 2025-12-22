package io.github.openflocon.flocondesktop.features.files.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.filter
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
    onBack: () -> Unit,
    onDeleteContent: () -> Unit,
    onRefresh: () -> Unit,
    onToggleMultiSelection: () -> Unit,
    onDeleteSelectedFiles: () -> Unit,
    multiSelectionEnabled: Boolean,
    selectedCount: Int,
    updateWithFoldersSize: (Boolean) -> Unit,
    filterBar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    options: FilesStateUiModel.Options,
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
                text = if (multiSelectionEnabled && selectedCount > 0) "$selectedCount selected" else current?.name.orEmpty(),
                style = FloconTheme.typography.bodyMedium,
                maxLines = 1,
                color = FloconTheme.colorPalette.onPrimary,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp, horizontal = 8.dp),
            )

            FloconIconToggleButton(
                value = multiSelectionEnabled,
                onValueChange = { onToggleMultiSelection() },
                tooltip = "Multi-Selection", // TODO resource
            ) {
                FloconIcon(
                    imageVector = Icons.Outlined.CheckCircle
                )
            }

            FloconIconToggleButton(
                value = options.withFoldersSize,
                onValueChange = updateWithFoldersSize,
                tooltip = stringResource(Res.string.with_folders_size),
            ) {
                FloconIcon(
                    imageVector = Icons.Outlined.FolderOpen
                )
            }
            filterBar()
        },
        actions = {
            if (multiSelectionEnabled && selectedCount > 0) {
                 FloconIconButton(
                    imageVector = Icons.Outlined.Delete,
                    enabled = true,
                    onClick = onDeleteSelectedFiles,
                    tooltip = "Delete Selected"
                )
            } else {
                FloconIconButton(
                    imageVector = Icons.Outlined.Delete,
                    enabled = isDirectory,
                    onClick = onDeleteContent,
                    tooltip = "Delete Content"
                )
            }
            
            FloconIconButton(
                imageVector = Icons.Outlined.Refresh,
                enabled = isDirectory,
                onClick = onRefresh,
            )
        }
    )
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
            onToggleMultiSelection = {},
            onDeleteSelectedFiles = {},
            multiSelectionEnabled = false,
            selectedCount = 0,
            filterBar = {},
            modifier = Modifier.fillMaxWidth(),
            updateWithFoldersSize = {},
            options = previewFilesStateUiModel().options,
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
            onToggleMultiSelection = {},
            onDeleteSelectedFiles = {},
            multiSelectionEnabled = true,
            selectedCount = 2,
            filterBar = {
                Button(onClick = {}) {
                    Text(text = "Filter")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            updateWithFoldersSize = {},
            options = previewFilesStateUiModel().options,
        )
    }
}
