package io.github.openflocon.flocondesktop.features.files.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isShiftPressed
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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

@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun FileItemRow(
    file: FileUiModel,
    selected: Boolean,
    selectionEnabled: Boolean,
    onSelectionChange: (Boolean, Boolean) -> Unit, // selected, shiftPressed
    onClick: (FileUiModel) -> Unit,
    onContextualAction: (FileUiModel, FileUiModel.ContextualAction.Action) -> Unit,
    modifier: Modifier = Modifier,
) {
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
        val interactionSource = remember { MutableInteractionSource() }
        Row(
            modifier = modifier
                .clickable { onClick(file) }
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimatedVisibility(visible = selectionEnabled) {
                Box(
                    modifier = Modifier.padding(end = 16.dp)
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    if (event.type == PointerEventType.Press && event.changes.first().pressed) {
                                        val shiftPressed = event.keyboardModifiers.isShiftPressed
                                        // We handle the click here for the checkbox area
                                        // But FloconCheckbox might capture it?
                                        // Let's rely on FloconCheckbox onCheckedChange if possible,
                                        // but it doesn't give modifiers.
                                        // So we wrap it.
                                    }
                                }
                            }
                        }
                ) {
                   // actually simpler: FloconCheckbox doesn't support modifiers in callback.
                   // We can use a custom Clickable modifier that checks keys?
                   // Or just use the Box wrapper for the click itself and ignore FloconCheckbox internal click?
                   // FloconCheckbox usually consumes click.

                   // Using a workaround: Wrap Checkbox in a Box that intercepts clicks?
                   // No, let's just use FloconCheckbox and maybe we can use `LocalInputModeManager` or similar? No.
                   // Let's use `createPointerInput` on the Checkbox to detect the modifier on Press
                   // and store it in a generic variable, then use it in onCheckedChange?
                   // A bit hacky.

                   // Better approach for Desktop:
                   // Use `onClick` with `PointerMatcher` if available, or `onPointerEvent`.
                   // Let's try `PointerInput`.
                }
                
                // Let's use a simpler approach:
                // Provide a lambda that captures standard click, and use a separate modifier for Shift+Click on the checkbox area?
                // Or: Make the checkbox not clickable (enabled=false?) and handle click on parent Box?
                // No, checkbox visual state handles animation.

                // OK, let's go with:
                var shiftPressed by remember { mutableStateOf(false) }
                
                FloconCheckbox(
                    checked = selected,
                    onCheckedChange = { onSelectionChange(it, shiftPressed) },
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .onPointerEvent(PointerEventType.Press) {
                            shiftPressed = it.keyboardModifiers.isShiftPressed
                        }
                )
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
            selected = false,
            selectionEnabled = false,
            onSelectionChange = { _, _ -> },
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
        sizeFormatted = "1 MB",
        contextualActions = emptyList(),
        dateFormatted = "2022-01-01 12:10",
    )
    FloconTheme {
        FileItemRow(
            file = file,
            selected = false,
            selectionEnabled = true,
            onSelectionChange = { _, _ -> },
            onClick = {},
            onContextualAction = { _, _ -> },
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
                    selected = false,
                    selectionEnabled = false,
                    onSelectionChange = { _, _ -> },
                    onClick = {},
                    onContextualAction = { _, _ -> },
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
                    selected = true,
                    selectionEnabled = true,
                    onSelectionChange = { _, _ -> },
                    onClick = {},
                    onContextualAction = { _, _ -> },
                )
            }
        }
    }
}
