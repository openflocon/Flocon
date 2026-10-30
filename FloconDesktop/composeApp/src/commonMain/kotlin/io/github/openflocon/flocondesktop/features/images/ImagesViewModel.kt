package io.github.openflocon.flocondesktop.features.images

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.images.usecase.ObserveImagesUseCase
import io.github.openflocon.domain.images.usecase.ResetCurrentDeviceImagesUseCase
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.features.images.mapper.buildImageViewer
import io.github.openflocon.flocondesktop.features.images.mapper.filterBy
import io.github.openflocon.flocondesktop.features.images.mapper.toUi
import io.github.openflocon.flocondesktop.features.images.model.ImageViewerUiModel
import io.github.openflocon.flocondesktop.features.images.model.ImagesDisplayModeUiModel
import io.github.openflocon.flocondesktop.features.images.model.ImagesStateUiModel
import io.github.openflocon.library.designsystem.common.copyToClipboard
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ImagesViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val observeImagesUseCase: ObserveImagesUseCase,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val resetCurrentDeviceImagesUseCase: ResetCurrentDeviceImagesUseCase,
) : ViewModel() {

    private val filter = MutableStateFlow("")

    private val _displayMode = MutableStateFlow(ImagesDisplayModeUiModel.Grid)
    val displayMode: StateFlow<ImagesDisplayModeUiModel> = _displayMode

    val state: StateFlow<ImagesStateUiModel> =
        combine(
            observeImagesUseCase(),
            filter,
        ) { images, filter ->
            images.filterBy(filter)
        }.map {
            if (it.isEmpty()) {
                ImagesStateUiModel.Empty
            } else {
                ImagesStateUiModel.WithImages(
                    images = it.map { image ->
                        image.toUi()
                    }.toImmutableList(),
                )
            }
        }.flowOn(dispatcherProvider.viewModel)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ImagesStateUiModel.Idle)

    private val selectedImageId = MutableStateFlow<String?>(null)

    val viewer: StateFlow<ImageViewerUiModel?> =
        combine(state, selectedImageId) { state, selectedId ->
            if (state is ImagesStateUiModel.WithImages && selectedId != null) {
                buildImageViewer(images = state.images, selectedId = selectedId)
            } else {
                null
            }
        }.stateInWhileSubscribed(null)

    init {
        // The selected image disappeared (cleared, filtered out): forget it so the viewer stays closed.
        combine(state, selectedImageId) { state, selectedId -> state to selectedId }
            .onEach { (state, selectedId) ->
                val stillThere = state is ImagesStateUiModel.WithImages && state.images.any { it.id == selectedId }
                if (selectedId != null && state !is ImagesStateUiModel.Idle && !stillThere) {
                    selectedImageId.value = null
                }
            }
            .launchIn(viewModelScope)
    }

    fun onImageSelected(id: String) {
        selectedImageId.value = id
    }

    fun onViewerClosed() {
        selectedImageId.value = null
    }

    fun onCopyImageUrl(url: String) {
        copyToClipboard(url)
    }

    fun onVisible() {
    }

    fun onNotVisible() {
    }

    fun onDisplayModeChanged(mode: ImagesDisplayModeUiModel) {
        _displayMode.value = mode
    }

    fun onFilterChanged(text: String) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            filter.emit(text)
        }
    }

    fun reset() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            resetCurrentDeviceImagesUseCase()
        }
    }
}
