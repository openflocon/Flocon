package io.github.openflocon.flocondesktop.features.files.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Drafts
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.common.ui.ContextualItem
import io.github.openflocon.flocondesktop.common.ui.ContextualView
import io.github.openflocon.flocondesktop.features.files.model.FilePathUiModel
import io.github.openflocon.flocondesktop.features.files.model.FileTypeUiModel
import io.github.openflocon.flocondesktop.features.files.model.FileUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FileItemRow(
    file: FileUiModel,
    onClick: (FileUiModel) -> Unit,
    onContextualAction: (FileUiModel, FileUiModel.ContextualAction.Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    ContextualView(
        items = file.contextualActions.map { action ->
            ContextualItem(
                text = action.text,
                onClick = {
                    onContextualAction(
                        file,
                        action.id,
                    )
                }
            )
        },
    ) {
        Row(
            modifier = modifier
                .clip(FloconTheme.shapes.medium)
                .clickable { onClick(file) }
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FloconIcon(
                imageVector = file.icon,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Nom du fichier/dossier
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = file.name,
                    style = FloconTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                // Informations additionnelles (taille, date de modification)
                // if (!file.isDirectory) {
                //    Text(
                //        text = "${viewModel.formatFileSize(file.size)}",
                //        style = FloconTheme.typography.bodySmall,
                //        color = FloconTheme.colorPalette.onSurfaceVariant
                //    )
                // }
                // Text(
                //    text = viewModel.formatLastModifiedDate(file.lastModified),
                //    style = FloconTheme.typography.bodySmall,
                //    color = FloconTheme.colorPalette.onSurfaceVariant
                // )
            }

            // Flèche si c'est un dossier (pour indiquer qu'on peut y naviguer)
            if (file.isDirectory) {
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    modifier = Modifier.size(24.dp),
                    tint = FloconTheme.colorPalette.onPrimary,
                    contentDescription = null,
                )
            }
        }
    }
}

@Preview
@Composable
private fun FileItemRowPreview_folder() {
    val file = FileUiModel(
        name = "MyFile",
        type = FileTypeUiModel.Folder,
        path = FilePathUiModel.Constants.CachesDir,
        icon = Icons.Outlined.Folder,
        size = 1024L,
        contextualActions = emptyList(),
    )
    FloconTheme {
        FileItemRow(
            file = file,
            onClick = {},
            onContextualAction = { _, _ -> },
        )
    }
}

@Preview
@Composable
private fun FileItemRowPreview_file() {
    val file = FileUiModel(
        name = "MyFile",
        type = FileTypeUiModel.Other,
        path = FilePathUiModel.Real("absolutePath"),
        icon = Icons.Outlined.Drafts,
        size = 1024L,
        contextualActions = emptyList(),
    )
    FloconTheme {
        FileItemRow(
            file = file,
            onClick = {},
            onContextualAction = { _, _ -> },
        )
    }
}
