package io.github.openflocon.flocondesktop.features.files.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.chevron_right
import flocondesktop.composeapp.generated.resources.file
import flocondesktop.composeapp.generated.resources.folder
import io.github.openflocon.flocondesktop.common.ui.ContextualItem
import io.github.openflocon.flocondesktop.common.ui.ContextualView
import io.github.openflocon.flocondesktop.features.files.ui.model.FilePathUiModel
import io.github.openflocon.flocondesktop.features.files.ui.model.FileTypeUiModel
import io.github.openflocon.flocondesktop.features.files.ui.model.FileUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FileItemRow(
    file: FileUiModel,
    onClick: (FileUiModel) -> Unit,
    onContextualAction: (FileUiModel, FileUiModel.ContextualAction.Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    ContextualView(
        file.contextualActions.map {
            ContextualItem(
                id = it.id.name,
                text = it.text,
            )
        },
        onSelect = {
            onContextualAction(
                file,
                FileUiModel.ContextualAction.Action.valueOf(it.id),
            )
        },
    ) {
        Row(
            modifier =
            modifier
                .clickable {
                    onClick(file)
                }.padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(file.icon),
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(FloconTheme.colorScheme.onSurface),
                contentDescription = null,
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Nom du fichier/dossier
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.name,
                    style = FloconTheme.typography.bodyLarge,
                    maxLines = 1,
                    color = FloconTheme.colorScheme.onSurface,
                    overflow = TextOverflow.Ellipsis,
                )
                // Informations additionnelles (taille, date de modification)
                // if (!file.isDirectory) {
                //    Text(
                //        text = "${viewModel.formatFileSize(file.size)}",
                //        style = FloconTheme.typography.bodySmall,
                //        color = FloconTheme.colorScheme.onSurfaceVariant
                //    )
                // }
                // Text(
                //    text = viewModel.formatLastModifiedDate(file.lastModified),
                //    style = FloconTheme.typography.bodySmall,
                //    color = FloconTheme.colorScheme.onSurfaceVariant
                // )
            }

            // Flèche si c'est un dossier (pour indiquer qu'on peut y naviguer)
            if (file.isDirectory) {
                Image(
                    painter = painterResource(Res.drawable.chevron_right),
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(FloconTheme.colorScheme.onSurface),
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
        icon = Res.drawable.folder,
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
        icon = Res.drawable.file,
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
