package io.github.openflocon.flocondesktop.features.files.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.common.ui.FloconColors
import io.github.openflocon.flocondesktop.features.files.ui.model.FilePathUiModel
import io.github.openflocon.flocondesktop.features.files.ui.model.FileTypeUiModel
import io.github.openflocon.flocondesktop.features.files.ui.model.FileUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIconButton
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

    Column(
        modifier = modifier
            .background(FloconColors.pannel)
            .padding(vertical = 16.dp, horizontal = 16.dp),
    ) {
        Text(
            text = "Files",
            modifier = Modifier.padding(bottom = 12.dp),
            style = FloconTheme.typography.titleLarge,
            color = FloconTheme.colorPalette.onSurface,
        )

        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TopBarButton(
                onClick = onBack,
                enabled = hasParentFile,
                icon = Icons.AutoMirrored.Filled.ArrowBack,
            )
            Box(
                modifier = Modifier.weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(FloconTheme.colorPalette.onSurface.copy(alpha = 0.1f))
                    .padding(vertical = 4.dp, horizontal = 12.dp),
            ) {
                Text(
                    current?.name ?: "",
                    style = FloconTheme.typography.bodyLarge,
                    maxLines = 1,
                    color = FloconTheme.colorPalette.onSurface,
                )
            }
            FloconIconButton(
                imageVector = Icons.Outlined.Delete,
                enabled = isDirectory,
                onClick = onDeleteContent
            )
            FloconIconButton(
                imageVector = Icons.Outlined.Refresh,
                enabled = isDirectory,
                onClick = onRefresh
            )
        }
    }
}

@Composable
private fun TopBarButton(
    onClick: () -> Unit,
    enabled: Boolean,
    icon: ImageVector,
) {
    Box(
        modifier = Modifier.size(32.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(FloconColors.onSurface.copy(alpha = 0.1f))
            .clickable(onClick = onClick, enabled = enabled)
            .padding(6.dp)
            .graphicsLayer {
                alpha = if (enabled) {
                    1f
                } else {
                    0.5f
                }
            },
    ) {
        Icon(
            imageVector = icon,
            modifier = Modifier.fillMaxSize(),
            tint = FloconTheme.colorPalette.onSurface,
            contentDescription = null,
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
