package io.github.openflocon.flocondesktop.features.files.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.files.model.FilePathUiModel
import io.github.openflocon.flocondesktop.features.files.model.FileTypeUiModel
import io.github.openflocon.flocondesktop.features.files.model.FileUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconIconTonalButton
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FilesTopBar(
    current: FileUiModel?,
    onBack: () -> Unit,
    onDeleteContent: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
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
                    .weight(1f)
                    .padding(vertical = 4.dp, horizontal = 8.dp),
            )
        },
        actions = {
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

@Preview
@Composable
private fun FilesTopBarPreview_noParent() {
    FloconTheme {
        FilesTopBar(
            current = null,
            onBack = {},
            onRefresh = {},
            onDeleteContent = {},
            modifier = Modifier.fillMaxWidth(),
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
        size = 1024L,
        contextualActions = emptyList(),
    )
    FloconTheme {
        FilesTopBar(
            current = current,
            onBack = {},
            onRefresh = {},
            onDeleteContent = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
