package io.github.openflocon.flocondesktop.features.files.model

import io.github.openflocon.flocondesktop.features.network.list.model.SortedByUiModel

sealed interface FilesAction {
    data class FileClicked(val file: FileUiModel) : FilesAction
    data object NavigateUp : FilesAction
    data class ContextualAction(
        val file: FileUiModel,
        val action: FileUiModel.ContextualAction.Action,
    ) : FilesAction
    data object Refresh : FilesAction
    data object DeleteContent : FilesAction
    data class FilterTextChanged(val value: String) : FilesAction
    data class ClickOnSort(
        val column: FileColumnUiModel,
        val sortedBy: SortedByUiModel,
    ) : FilesAction
    data class UpdateWithFoldersSize(val value: Boolean) : FilesAction
    data object MultiSelect : FilesAction
    data object ClearMultiSelect : FilesAction
    data class SelectFile(
        val path: String,
        val selected: Boolean,
        val index: Int,
        val shiftHeld: Boolean,
    ) : FilesAction
    data object DeleteSelection : FilesAction
    data class SelectAll(val selectingAll: Boolean) : FilesAction
}
