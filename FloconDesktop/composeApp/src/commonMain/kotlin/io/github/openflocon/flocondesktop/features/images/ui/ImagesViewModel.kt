package io.github.openflocon.flocondesktop.features.images.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.florent37.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import com.florent37.flocondesktop.common.ui.feedback.FeedbackDisplayer
import com.florent37.flocondesktop.features.images.domain.ObserveImagesUseCase
import com.florent37.flocondesktop.features.images.domain.ResetCurrentDeviceImagesUseCase
import com.florent37.flocondesktop.features.images.ui.model.ImagesStateUiModel
import com.florent37.flocondesktop.features.images.ui.model.ImagesUiModel
import com.florent37.flocondesktop.features.network.ui.mapper.formatTimestamp
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ImagesViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val observeImagesUseCase: ObserveImagesUseCase,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val resetCurrentDeviceImagesUseCase: ResetCurrentDeviceImagesUseCase,
) : ViewModel() {
    val state: StateFlow<ImagesStateUiModel> =
        observeImagesUseCase()
            .map {
                if (it.isEmpty()) {
                    ImagesStateUiModel.Empty
                } else {
                    ImagesStateUiModel.WithImages(
                        images =
                        it.map { image ->
                            ImagesUiModel(
                                url = image.url,
                                downloadedAt = formatTimestamp(image.time),
                            )
                        },
                    )
                }
            }.flowOn(dispatcherProvider.viewModel)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ImagesStateUiModel.Idle)

    fun onVisible() {
    }

    fun onNotVisible() {
    }

    fun reset() {
        viewModelScope.launch {
            resetCurrentDeviceImagesUseCase()
        }
    }
}
