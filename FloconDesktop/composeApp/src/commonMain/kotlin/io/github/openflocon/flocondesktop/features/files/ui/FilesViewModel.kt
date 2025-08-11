package io.github.openflocon.flocondesktop.features.files.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.library.designsystem.common.copyToClipboard
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.ui.feedback.FeedbackDisplayer
import io.github.openflocon.domain.files.usecase.DeleteFileUseCase
import io.github.openflocon.domain.files.usecase.DeleteFolderContentUseCase
import io.github.openflocon.domain.files.usecase.ObserveFolderContentUseCase
import io.github.openflocon.domain.files.usecase.RefreshFolderContentUseCase
import io.github.openflocon.domain.files.models.FileDomainModel
import io.github.openflocon.flocondesktop.features.files.ui.mapper.buildContextualActions
import io.github.openflocon.flocondesktop.features.files.ui.mapper.toDomain
import io.github.openflocon.flocondesktop.features.files.ui.mapper.toUi
import io.github.openflocon.flocondesktop.features.files.ui.model.FilePathUiModel
import io.github.openflocon.flocondesktop.features.files.ui.model.FileTypeUiModel
import io.github.openflocon.flocondesktop.features.files.ui.model.FileUiModel
import io.github.openflocon.flocondesktop.features.files.ui.model.FileUiModel.ContextualAction
import io.github.openflocon.flocondesktop.features.files.ui.model.FilesStateUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FilesViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val observeFolderContentUseCase: ObserveFolderContentUseCase,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val deleteFileUseCase: DeleteFileUseCase,
    private val deleteFolderContentUseCase: DeleteFolderContentUseCase,
    private val refreshFolderContentUseCase: RefreshFolderContentUseCase,
) : ViewModel() {

    private fun defaultValue() = FilesStateUiModel(
        current = null,
        backStack = emptyList(),
        files =
            listOf(
                FileUiModel(
                    name = "Caches",
                    type = FileTypeUiModel.Folder,
                    path = FilePathUiModel.Constants.CachesDir,
                    size = 0L,
                    icon = Icons.Outlined.Folder,
                    contextualActions = buildContextualActions(
                        isConstant = true,
                        isFolder = true,
                    ),
                ),
                FileUiModel(
                    name = "Files",
                    type = FileTypeUiModel.Folder,
                    path = FilePathUiModel.Constants.FilesDir,
                    size = 0L,
                    icon = Icons.Outlined.Folder,
                    contextualActions = buildContextualActions(
                        isConstant = true,
                        isFolder = true,
                    ),
                ),
            ),
    )

    private data class SelectedFile(
        val backStack: List<FileDomainModel>,
        val current: FileDomainModel,
    )

    private val selectedFile = MutableStateFlow<SelectedFile?>(null)
    val state: StateFlow<FilesStateUiModel> =
        selectedFile
            .flatMapLatest { selectedFile ->
                if (selectedFile == null) {
                    flowOf(defaultValue())
                } else {
                    observeFolderContentUseCase(
                        selectedFile.current.path,
                        fetchScope = viewModelScope,
                    )
                        .map { files ->
                            FilesStateUiModel(
                                backStack = selectedFile.backStack.map { it.toUi() },
                                current = selectedFile.current.toUi(),
                                files = (files ?: emptyList()).map { it.toUi() },
                            )
                        }
                }
            }.flowOn(dispatcherProvider.viewModel)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), defaultValue())

    fun onNavigateUp() {
        selectedFile.update {
            if (it == null) {
                it // no op
            } else if (it.backStack.isEmpty()) { // go back to root
                null
            } else {
                val last = it.backStack.last()
                it.copy(
                    backStack = it.backStack.dropLast(1),
                    current = last,
                )
            }
        }
    }

    fun onFileClicked(fileUiModel: FileUiModel) {
        val newCurrent = fileUiModel.toDomain()
        when (fileUiModel.type) {
            FileTypeUiModel.Folder -> {
                selectedFile.update {
                    if (it == null) {
                        SelectedFile(
                            backStack = emptyList(),
                            current = newCurrent,
                        )
                    } else {
                        it.copy(
                            backStack = it.backStack + it.current,
                            current = newCurrent,
                        )
                    }
                }
            }

            FileTypeUiModel.Image,
            FileTypeUiModel.Video,
            FileTypeUiModel.Text,
            FileTypeUiModel.Other,
                -> {
                feedbackDisplayer.displayMessage("not implemented")
            }
        }
    }

    fun onRefresh() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val current = selectedFile.value?.current?.path ?: return@launch
            refreshFolderContentUseCase(path = current)
        }
    }

    fun onDeleteContent() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val current = selectedFile.value?.current?.path ?: return@launch
            deleteFolderContentUseCase(path = current)
        }
    }

    fun onContextualAction(file: FileUiModel, action: ContextualAction.Action) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            when (action) {
                ContextualAction.Action.Open -> onFileClicked(file)
                ContextualAction.Action.Delete -> {
                    val parent = selectedFile.value?.current?.path ?: return@launch
                    deleteFileUseCase(path = file.path.toDomain(), parentPath = parent)
                }

                ContextualAction.Action.DeleteContent -> {
                    deleteFolderContentUseCase(path = file.path.toDomain())
                }

                ContextualAction.Action.CopyPath -> {
                    (file.path as? FilePathUiModel.Real)?.let {
                        copyToClipboard(it.absolutePath)
                    }
                }
            }
        }
    }

    fun onVisible() {
    }

    fun onNotVisible() {
    }
}
