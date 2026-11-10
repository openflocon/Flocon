package io.github.openflocon.flocondesktop.features.files.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isShiftPressed
import androidx.compose.ui.input.pointer.onPointerEvent
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.general_copy_path
import flocondesktop.composeapp.generated.resources.general_delete
import flocondesktop.composeapp.generated.resources.general_delete_content
import flocondesktop.composeapp.generated.resources.general_open
import io.github.openflocon.flocondesktop.features.files.model.FilePathUiModel
import io.github.openflocon.flocondesktop.features.files.model.FileUiModel
import io.github.openflocon.library.designsystem.common.FloconContextMenuItem
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource

/**
 * Click behaviour shared by the list rows and the grid items: a click opens the entry (or toggles its selection in
 * multi-select mode, shift + click selects a range), a double click always opens it.
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun Modifier.fileClickable(
    file: FileUiModel,
    index: Int,
    multiSelect: Boolean,
    multiSelected: Boolean,
    interactionSource: MutableInteractionSource,
    onClick: (FileUiModel) -> Unit,
    onSelectFile: (path: String, selected: Boolean, index: Int, shiftHeld: Boolean) -> Unit,
): Modifier {
    val shiftPressed = remember { mutableStateOf(false) }
    return this
        .onPointerEvent(PointerEventType.Press) {
            shiftPressed.value = it.keyboardModifiers.isShiftPressed
        }
        .combinedClickable(
            interactionSource = interactionSource,
            // the hover background is the feedback
            indication = null,
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
            },
        )
}

@Composable
private fun contextualActionLabel(action: FileUiModel.ContextualAction.Action): String = when (action) {
    FileUiModel.ContextualAction.Action.Open -> stringResource(Res.string.general_open)
    FileUiModel.ContextualAction.Action.CopyPath -> stringResource(Res.string.general_copy_path)
    FileUiModel.ContextualAction.Action.DeleteContent -> stringResource(Res.string.general_delete_content)
    FileUiModel.ContextualAction.Action.Delete -> stringResource(Res.string.general_delete)
}

private fun contextualActionIcon(action: FileUiModel.ContextualAction.Action): ImageVector = when (action) {
    FileUiModel.ContextualAction.Action.Open -> Icons.Outlined.OpenInNew
    FileUiModel.ContextualAction.Action.CopyPath -> Icons.Outlined.ContentCopy
    FileUiModel.ContextualAction.Action.DeleteContent -> Icons.Outlined.DeleteSweep
    FileUiModel.ContextualAction.Action.Delete -> Icons.Outlined.Delete
}

/** Items of the right click menu of an entry. */
@Composable
internal fun rememberFileContextMenuItems(
    file: FileUiModel,
    onContextualAction: (FileUiModel, FileUiModel.ContextualAction.Action) -> Unit,
): ImmutableList<FloconContextMenuItem> {
    val callback by rememberUpdatedState(onContextualAction)
    val labels = FileUiModel.ContextualAction.Action.entries.associateWith { contextualActionLabel(it) }
    return remember(file, labels) {
        file.contextualActions.map { action ->
            FloconContextMenuItem.Item(
                label = labels.getValue(action.id),
                onClick = { callback(file, action.id) },
            )
        }.toImmutableList()
    }
}

/** Dropdown with the contextual actions of an entry, opened from its "⋯" button. */
@Composable
internal fun FileActionsMenu(
    file: FileUiModel,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onContextualAction: (FileUiModel, FileUiModel.ContextualAction.Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    FloconDropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        file.contextualActions.forEach { action ->
            FloconDropdownMenuItem(
                text = contextualActionLabel(action.id),
                leadingIcon = contextualActionIcon(action.id),
                onClick = {
                    onDismissRequest()
                    onContextualAction(file, action.id)
                },
            )
        }
    }
}
