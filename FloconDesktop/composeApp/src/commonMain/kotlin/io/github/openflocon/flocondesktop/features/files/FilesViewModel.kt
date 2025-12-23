package io.github.openflocon.flocondesktop.features.files

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.ByteFormatter
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.combines
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.files.models.FileDomainModel
import io.github.openflocon.domain.files.models.FilePathDomainModel
import io.github.openflocon.domain.files.usecase.DeleteFileUseCase
import io.github.openflocon.domain.files.usecase.DeleteFilesUseCase
import io.github.openflocon.domain.files.usecase.DeleteFolderContentUseCase
import io.github.openflocon.domain.files.usecase.DownloadFileUseCase
import io.github.openflocon.domain.files.usecase.ObserveFolderContentUseCase
import io.github.openflocon.domain.files.usecase.ObserveWithFoldersSizeUseCase
import io.github.openflocon.domain.files.usecase.RefreshFolderContentUseCase
import io.github.openflocon.domain.files.usecase.UpdateWithFoldersSizeUseCase
import io.github.openflocon.flocondesktop.common.utils.OpenFile
import io.github.openflocon.flocondesktop.features.files.mapper.buildContextualActions
import io.github.openflocon.flocondesktop.features.files.mapper.toDomain
import io.github.openflocon.flocondesktop.features.files.mapper.toUi
import io.github.openflocon.flocondesktop.features.files.model.FileColumnUiModel
import io.github.openflocon.flocondesktop.features.files.model.FilePathUiModel
import io.github.openflocon.flocondesktop.features.files.model.FileTypeUiModel
import io.github.openflocon.flocondesktop.features.files.model.FileUiModel
import io.github.openflocon.flocondesktop.features.files.model.FilesAction
import io.github.openflocon.flocondesktop.features.files.model.FilesHeaderStateUiModel
import io.github.openflocon.flocondesktop.features.files.model.FilesStateUiModel
import io.github.openflocon.flocondesktop.features.network.list.model.SortedByUiModel
import io.github.openflocon.library.designsystem.common.copyToClipboard
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
    private val deleteFilesUseCase: DeleteFilesUseCase,
    private val downloadFileUseCase: DownloadFileUseCase,
    private val deleteFolderContentUseCase: DeleteFolderContentUseCase,
    private val refreshFolderContentUseCase: RefreshFolderContentUseCase,
    private val updateWithFoldersSizeUseCase: UpdateWithFoldersSizeUseCase,
    private val observeWithFoldersSizeUseCase: ObserveWithFoldersSizeUseCase,
) : ViewModel() {

    private val _filterText = mutableStateOf("")
    val filterText: State<String> = _filterText

    private val sortedBy = MutableStateFlow<FilesHeaderStateUiModel.SortedBy?>(null)

    // Multi-selection state
    private val _selecting = MutableStateFlow(false)
    private val _multiSelectedPaths = MutableStateFlow<Set<String>>(emptySet())
    private var _lastSelectedIndex: Int? = null

    private val options = observeWithFoldersSizeUseCase().map { withFoldersSize ->
        FilesStateUiModel.Options(
            withFoldersSize = withFoldersSize,
        )
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000),
        FilesStateUiModel.Options(
            withFoldersSize = false,
        )
    )

    private fun defaultValue() = FilesStateUiModel(
        current = null,
        backStack = emptyList(),
        files =
        listOf(
            FileUiModel(
                name = "Caches",
                type = FileTypeUiModel.Folder,
                path = FilePathUiModel.Constants.CachesDir,
                sizeFormatted = null,
                icon = Icons.Outlined.Folder,
                dateFormatted = null,
                contextualActions = buildContextualActions(
                    isConstant = true,
                    isFolder = true,
                ),
            ),
            FileUiModel(
                name = "Files",
                type = FileTypeUiModel.Folder,
                path = FilePathUiModel.Constants.FilesDir,
                sizeFormatted = null,
                icon = Icons.Outlined.Folder,
                dateFormatted = null,
                contextualActions = buildContextualActions(
                    isConstant = true,
                    isFolder = true,
                ),
            ),
        ),
        headerState = FilesHeaderStateUiModel(
            sortedBy = sortedBy.value,
            totalSizeFormatted = null,
        ),
        options = options.value,
        isSelecting = _selecting.value,
        multiSelectedPaths = _multiSelectedPaths.value,
        selectingAll = false,
        numberOfFiles = null,
        canSelect = false,
    )

    private data class SelectedFile(
        val backStack: List<FileDomainModel>,
        val current: FileDomainModel,
    )

    private val selectedFile = MutableStateFlow<SelectedFile?>(null)
    val state: StateFlow<FilesStateUiModel> =
        combines(
            selectedFile,
            sortedBy,
            options,
            _selecting,
            _multiSelectedPaths,
        )
            .flatMapLatest { (selectedFile, sortedBy, options, selecting, multiSelectedPaths) ->
                if (selectedFile == null) {
                    flowOf(defaultValue().copy(isSelecting = selecting, multiSelectedPaths = multiSelectedPaths))
                } else {
                    combines(
                        observeFolderContentUseCase(
                            selectedFile.current.path,
                            fetchScope = viewModelScope,
                        ),
                        snapshotFlow { _filterText.value },
                        observeWithFoldersSizeUseCase(),
                    ).map { (files, filter, withFoldersSize) ->
                        val filtered = if (filter.isNotBlank()) {
                            files?.filter { it.name.contains(filter, ignoreCase = true) }
                        } else {
                            files
                        }
                        val sorted = filtered?.let { sort(it, sortedBy) }
                        FilesStateUiModel(
                            backStack = selectedFile.backStack.map { it.toUi(options.withFoldersSize) },
                            current = selectedFile.current.toUi(options.withFoldersSize),
                            files = (
                                sorted
                                    ?: emptyList()
                                ).map { it.toUi(options.withFoldersSize) },
                            numberOfFiles = sorted?.size,
                            headerState = FilesHeaderStateUiModel(
                                sortedBy = sortedBy,
                                totalSizeFormatted = computeTotalSize(
                                    options.withFoldersSize,
                                    sorted
                                )
                            ),
                            canSelect = true,
                            options = options,
                            isSelecting = selecting,
                            multiSelectedPaths = multiSelectedPaths,
                            selectingAll = multiSelectedPaths.isNotEmpty() && multiSelectedPaths.size == sorted?.size
                        )
                    }
                }
            }
            .flowOn(dispatcherProvider.viewModel)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), defaultValue())

    private fun computeTotalSize(
        withFoldersSize: Boolean,
        sorted: List<FileDomainModel>?
    ): String? = if (!withFoldersSize) null
    else sorted?.sumOf { it.size }?.let {
        ByteFormatter.formatBytes(it)
    }

    private fun sort(
        files: List<FileDomainModel>,
        sortedBy: FilesHeaderStateUiModel.SortedBy?
    ): List<FileDomainModel> {
        if (sortedBy == null) {
            return files
        }

        return when (sortedBy.column) {
            FileColumnUiModel.Name -> {
                when (sortedBy.sortedBy) {
                    SortedByUiModel.Enabled.Ascending -> files.sortedBy { it.name }
                    SortedByUiModel.Enabled.Descending -> files.sortedByDescending { it.name }
                }
            }

            FileColumnUiModel.Date -> {
                when (sortedBy.sortedBy) {
                    SortedByUiModel.Enabled.Ascending -> files.sortedBy { it.lastModified }
                    SortedByUiModel.Enabled.Descending -> files.sortedByDescending { it.lastModified }
                }
            }

            FileColumnUiModel.Size -> {
                when (sortedBy.sortedBy) {
                    SortedByUiModel.Enabled.Ascending -> files.sortedBy { it.size }
                    SortedByUiModel.Enabled.Descending -> files.sortedByDescending { it.size }
                }
            }
        }
    }

    private fun onNavigateUp() {
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
        _filterText.value = "" // clear
        // Clear selection when navigating
        onClearMultiSelect()
    }

    private fun onFilterTextChanged(value: String) {
        _filterText.value = value
    }

    private fun updateWithFoldersSize(value: Boolean) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            updateWithFoldersSizeUseCase(value)
            onRefresh() // then perform a refresh
        }
    }

    private fun onFileClicked(fileUiModel: FileUiModel) {
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
                // Clear selection when navigating into folder
                onClearMultiSelect()
            }

            FileTypeUiModel.Image,
            FileTypeUiModel.Video,
            FileTypeUiModel.Text,
            FileTypeUiModel.Other,
            -> {
                (fileUiModel.path.toDomain() as? FilePathDomainModel.Real)?.let {
                    viewModelScope.launch(dispatcherProvider.viewModel) {
                        downloadFileUseCase(it).alsoSuccess {
                            OpenFile.openFileOnDesktop(it.localPath).alsoFailure {
                                feedbackDisplayer.displayMessage(
                                    message = it.message,
                                    type = FeedbackDisplayer.MessageType.Error,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun onRefresh() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val current = selectedFile.value?.current?.path ?: return@launch
            refreshFolderContentUseCase(path = current)
        }
    }

    private fun clickOnSort(column: FileColumnUiModel, sortedBy: SortedByUiModel) {
        when (sortedBy) {
            SortedByUiModel.None -> {
                this.sortedBy.value = null
            }

            is SortedByUiModel.Enabled -> {
                this.sortedBy.value = FilesHeaderStateUiModel.SortedBy(
                    column = column,
                    sortedBy = sortedBy,
                )
            }
        }
    }

    private fun onDeleteContent() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val current = selectedFile.value?.current?.path ?: return@launch
            deleteFolderContentUseCase(path = current)
        }
    }

    private fun onContextualAction(file: FileUiModel, action: FileUiModel.ContextualAction.Action) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            when (action) {
                FileUiModel.ContextualAction.Action.Open -> onFileClicked(file)
                FileUiModel.ContextualAction.Action.Delete -> {
                    val parent = selectedFile.value?.current?.path ?: return@launch
                    deleteFileUseCase(path = file.path.toDomain(), parentPath = parent)
                }

                FileUiModel.ContextualAction.Action.DeleteContent -> {
                    deleteFolderContentUseCase(path = file.path.toDomain())
                }

                FileUiModel.ContextualAction.Action.CopyPath -> {
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

    fun onAction(action: FilesAction) {
        when (action) {
            is FilesAction.FileClicked -> onFileClicked(action.file)
            is FilesAction.NavigateUp -> onNavigateUp()
            is FilesAction.ContextualAction -> onContextualAction(action.file, action.action)
            is FilesAction.Refresh -> onRefresh()
            is FilesAction.DeleteContent -> onDeleteContent()
            is FilesAction.FilterTextChanged -> onFilterTextChanged(action.value)
            is FilesAction.ClickOnSort -> clickOnSort(action.column, action.sortedBy)
            is FilesAction.UpdateWithFoldersSize -> updateWithFoldersSize(action.value)
            is FilesAction.MultiSelect -> onMultiSelect()
            is FilesAction.ClearMultiSelect -> onClearMultiSelect()
            is FilesAction.SelectFile -> onSelectFile(action.path, action.selected, action.index, action.shiftHeld)
            is FilesAction.DeleteSelection -> onDeleteSelection()
            is FilesAction.SelectAll -> onSelectAll(action.selectingAll)
        }
    }

    // Multi-selection methods
    private fun onMultiSelect() {
        _selecting.update { !it }
        if (!_selecting.value) {
            _multiSelectedPaths.value = emptySet()
            _lastSelectedIndex = null
        }
    }

    private fun onClearMultiSelect() {
        _selecting.value = false
        _multiSelectedPaths.value = emptySet()
        _lastSelectedIndex = null
    }

    private fun onSelectFile(path: String, selected: Boolean, index: Int, shiftHeld: Boolean) {
        val currentFiles = state.value.files
        
        if (shiftHeld && _lastSelectedIndex != null) {
            // Shift+Click: select range
            val startIndex = minOf(_lastSelectedIndex!!, index)
            val endIndex = maxOf(_lastSelectedIndex!!, index)
            val pathsInRange = currentFiles
                .subList(startIndex, endIndex + 1)
                .mapNotNull { file ->
                    (file.path as? FilePathUiModel.Real)?.absolutePath
                }
                .toSet()
            _multiSelectedPaths.update { it + pathsInRange }
        } else {
            // Regular click: toggle single item
            _multiSelectedPaths.update {
                if (selected) {
                    it + path
                } else {
                    it - path
                }
            }
            _lastSelectedIndex = index
        }
    }

    private fun onSelectAll(selectAll: Boolean) {
        if(selectAll) {
            _multiSelectedPaths.update {
                state.value.files
                    .mapNotNull { (it.path as? FilePathUiModel.Real)?.absolutePath }
                    .toSet()
            }
        } else {
            _multiSelectedPaths.update {
                emptySet()
            }
        }
    }

    private fun onDeleteSelection() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val parent = selectedFile.value?.current?.path ?: return@launch
            val selectedPaths = _multiSelectedPaths.value
                .map { FilePathDomainModel.Real(it) }
            
            deleteFilesUseCase(
                paths = selectedPaths,
                parentPath = parent
            )
            
            // Clear selection after deletion
            onClearMultiSelect()
            
            // Refresh the folder content
            onRefresh()
        }
    }
}
