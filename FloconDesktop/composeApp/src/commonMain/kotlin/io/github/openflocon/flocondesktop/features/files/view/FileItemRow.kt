package io.github.openflocon.flocondesktop.features.files.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isShiftPressed
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.common.ui.ContextualView
import io.github.openflocon.flocondesktop.features.files.model.FilePathUiModel
import io.github.openflocon.flocondesktop.features.files.model.FileTypeUiModel
import io.github.openflocon.flocondesktop.features.files.model.FileUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.common.FloconContextMenuItem
import io.github.openflocon.library.designsystem.components.FloconCheckbox
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconSurface
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun FileItemRow(
    file: FileUiModel,
    index: Int,
    multiSelect: Boolean,
    multiSelected: Boolean,
    onClick: (FileUiModel) -> Unit,
    onContextualAction: (FileUiModel, FileUiModel.ContextualAction.Action) -> Unit,
    onSelectFile: (path: String, selected: Boolean, index: Int, shiftHeld: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shiftPressed = remember { mutableStateOf(false) }
    
    ContextualView(
        items = file.contextualActions.map { action ->
            FloconContextMenuItem.Item(
                label = action.text,
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
                .onPointerEvent(PointerEventType.Press) {
                    shiftPressed.value = it.keyboardModifiers.isShiftPressed
                }
                .combinedClickable(
                    onClick = {
                        if (multiSelect) {
                            val path = (file.path as? FilePathUiModel.Real)?.absolutePath
                            if (path != null) {
                                onSelectFile(path, !multiSelected, index, shiftPressed.value)
                            }
                        } else {
                            onClick(file)
                        }
                    },
                    onDoubleClick = {
                        // Always allow navigation/opening on double-click
                        onClick(file)
                    }
                )
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimatedVisibility(multiSelect) {
                Row {
                    FloconCheckbox(
                        checked = multiSelected,
                        onCheckedChange = { selected ->
                            val path = (file.path as? FilePathUiModel.Real)?.absolutePath
                            if (path != null) {
                                onSelectFile(path, selected, index, shiftPressed.value)
                            }
                        },
                        uncheckedColor = FloconTheme.colorPalette.secondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            
            FloconIcon(
                imageVector = file.icon,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = file.name,
                modifier = Modifier.weight(1f),
                style = FloconTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = file.dateFormatted ?: "",
                style = FloconTheme.typography.bodySmall,
                maxLines = 1,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(150.dp),
                color = FloconTheme.colorPalette.onSecondary.copy(alpha = 0.6f),
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = file.sizeFormatted ?: "",
                textAlign = TextAlign.End,
                style = FloconTheme.typography.bodySmall,
                maxLines = 1,
                modifier = Modifier.width(100.dp),
                color = FloconTheme.colorPalette.onSecondary.copy(alpha = 0.6f),
                overflow = TextOverflow.Ellipsis,
            )

            // Flèche si c'est un dossier (pour indiquer qu'on peut y naviguer)
            if (file.isDirectory) {
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    modifier = Modifier.size(24.dp),
                    tint = FloconTheme.colorPalette.onPrimary,
                    contentDescription = null,
                )
            } else {
                Spacer(modifier = Modifier.size(24.dp))
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
        sizeFormatted = "1 MB",
        contextualActions = emptyList(),
        dateFormatted = "2022-01-01 12:10",
    )
    FloconTheme {
        FileItemRow(
            file = file,
            index = 0,
            multiSelect = false,
            multiSelected = false,
            onClick = {},
            onContextualAction = { _, _ -> },
            onSelectFile = { _, _, _, _ -> },
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
        sizeFormatted = "1 MB",
        contextualActions = emptyList(),
        dateFormatted = "2022-01-01 12:10",
    )
    FloconTheme {
        FileItemRow(
            file = file,
            index = 0,
            multiSelect = true,
            multiSelected = true,
            onClick = {},
            onContextualAction = { _, _ -> },
            onSelectFile = { _, _, _, _ -> },
        )
    }
}

@Preview
@Composable
private fun FileItemRowPreview() {
    FloconTheme {
        FloconSurface {
            Column {
                FileItemRow(
                    file = FileUiModel(
                        name = "MyFile",
                        type = FileTypeUiModel.Folder,
                        path = FilePathUiModel.Constants.CachesDir,
                        icon = Icons.Outlined.Folder,
                        sizeFormatted = "1 MB",
                        contextualActions = emptyList(),
                        dateFormatted = "2022-01-01 12:10",
                    ),
                    index = 0,
                    multiSelect = false,
                    multiSelected = false,
                    onClick = {},
                    onContextualAction = { _, _ -> },
                    onSelectFile = { _, _, _, _ -> },
                )
                FileItemRow(
                    file = FileUiModel(
                        name = "MyFile",
                        type = FileTypeUiModel.Other,
                        path = FilePathUiModel.Real("absolutePath"),
                        icon = Icons.Outlined.Drafts,
                        sizeFormatted = "1 MB",
                        contextualActions = emptyList(),
                        dateFormatted = "2022-01-01 12:10",
                    ),
                    index = 1,
                    multiSelect = true,
                    multiSelected = false,
                    onClick = {},
                    onContextualAction = { _, _ -> },
                    onSelectFile = { _, _, _, _ -> },
                )
            }
        }
    }
}

