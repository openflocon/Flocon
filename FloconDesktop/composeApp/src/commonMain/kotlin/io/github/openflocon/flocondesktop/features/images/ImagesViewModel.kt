package io.github.openflocon.flocondesktop.features.images

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.images.models.DeviceImageDomainModel
import io.github.openflocon.domain.images.usecase.ObserveImagesUseCase
import io.github.openflocon.domain.images.usecase.ResetCurrentDeviceImagesUseCase
import io.github.openflocon.flocondesktop.features.images.mapper.filterBy
import io.github.openflocon.flocondesktop.features.images.mapper.toUi
import io.github.openflocon.flocondesktop.features.images.model.ImagesStateUiModel
import io.github.openflocon.flocondesktop.features.images.model.ImagesUiModel
import io.github.openflocon.flocondesktop.features.network.list.mapper.formatTimestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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

    private val filter = MutableStateFlow("")

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
                    },
                )
            }
        }.flowOn(dispatcherProvider.viewModel)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ImagesStateUiModel.Idle)

    fun onVisible() {
    }

    fun onNotVisible() {
    }

    fun onFilterChanged(text: String) {
        viewModelScope.launch(dispatcherProvider.viewModel) {

        }
    }

    fun reset() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            resetCurrentDeviceImagesUseCase()
        }
    }
}

