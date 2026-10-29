package io.github.openflocon.flocondesktop.features.files.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.files_counter
import flocondesktop.composeapp.generated.resources.filter
import flocondesktop.composeapp.generated.resources.menu_item_files
import flocondesktop.composeapp.generated.resources.with_folders_size
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDocButton
import io.github.openflocon.flocondesktop.features.files.model.FilePathUiModel
import io.github.openflocon.flocondesktop.features.files.model.FileTypeUiModel
import io.github.openflocon.flocondesktop.features.files.model.FileUiModel
import io.github.openflocon.flocondesktop.features.files.model.FilesStateUiModel
import io.github.openflocon.flocondesktop.features.files.model.previewFilesStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconIconToggleButton
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun FilesTopBar(
    current: FileUiModel?,
    numberOfFiles: Int?,
    onBack: () -> Unit,
    onDeleteContent: () -> Unit,
    onRefresh: () -> Unit,
    updateWithFoldersSize: (Boolean) -> Unit,
    filterBar: @Composable RowScope.() -> Unit,
    options: FilesStateUiModel.Options,
    canSelect: Boolean,
    selecting: Boolean,
    selectedCount: Int,
    onDeleteSelection: () -> Unit,
    onMultiSelect: () -> Unit,
    onClearMultiSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasParentFile = current != null
    val isDirectory = current?.isDirectory == true

    val title = stringResource(Res.string.menu_item_files)
    val path = when (val path = current?.path) {
        is FilePathUiModel.Real -> path.absolutePath
        is FilePathUiModel.Root -> current?.name.orEmpty()
        null -> null
    }
    val count = numberOfFiles?.let { pluralStringResource(Res.plurals.files_counter, it, it) }
    val subtitle = listOfNotNull(path, count).joinToString(separator = " · ").ifEmpty { null }

    FloconPageTopBar(
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        headerActions = { FeatureDocButton(doc = FeatureDoc.Files) },
        leading = {
            FloconIconButton(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                enabled = hasParentFile,
                onClick = onBack,
            )
        },
        filterBar = filterBar,
        actions = {
            AnimatedVisibility(canSelect) {
                Selecting(
                    selecting = selecting,
                    selectedCount = selectedCount,
                    onDeleteSelection = onDeleteSelection,
                    onMultiSelect = onMultiSelect,
                    onClearMultiSelect = onClearMultiSelect,
                )
            }

            FloconIconToggleButton(
                value = options.withFoldersSize,
                onValueChange = updateWithFoldersSize,
                tooltip = stringResource(Res.string.with_folders_size),
            ) {
                FloconIcon(
                    imageVector = Icons.Outlined.RemoveRedEye
                )
            }

            FloconIconButton(
                imageVector = Icons.Outlined.Refresh,
                enabled = isDirectory,
                onClick = onRefresh,
            )

            // Delete folder content button, last
            FloconIconButton(
                imageVector = Icons.Outlined.Delete,
                enabled = isDirectory,
                onClick = onDeleteContent,
            )
        }
    )
}

@Composable
private fun Selecting(
    selecting: Boolean,
    selectedCount: Int,
    onDeleteSelection: () -> Unit,
    onClearMultiSelect: () -> Unit,
    onMultiSelect: () -> Unit,
    modifier: Modifier = Modifier,
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
                    color = FloconTheme.colors.accentSoft,
                    shape = FloconTheme.shapes.medium,
                ).padding(
                    horizontal = FloconTheme.spacing.small
                )
            ) {
                Text(
                    text = "$selectedCount selected",
                    style = FloconTheme.typography.bodyMedium,
                    color = FloconTheme.colors.accent,
                    modifier = Modifier.padding(horizontal = FloconTheme.spacing.small),
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
        path = FilePathUiModel.Root(id = "caches"),
        icon = Icons.Outlined.Folder,
        sizeFormatted = "10 KB",
        contextualActions = persistentListOf(),
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
                    Text(text = stringResource(Res.string.filter))
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
