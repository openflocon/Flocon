package io.github.openflocon.flocondesktop.features.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.analytics.usecase.ObserveCurrentDeviceAnalyticsContentUseCase
import io.github.openflocon.domain.analytics.usecase.RemoveAnalyticsItemUseCase
import io.github.openflocon.domain.analytics.usecase.RemoveAnalyticsItemsBeforeUseCase
import io.github.openflocon.domain.analytics.usecase.ResetCurrentDeviceSelectedAnalyticsUseCase
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.features.analytics.delegate.AnalyticsSelectorDelegate
import io.github.openflocon.flocondesktop.features.analytics.mapper.mapToUi
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsAction
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsContentStateUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsRowUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsScreenUiState
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsStateUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.DeviceAnalyticsUiModel
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AnalyticsViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val analyticsSelectorDelegate: AnalyticsSelectorDelegate,
    observeCurrentDeviceAnalyticsContentUseCase: ObserveCurrentDeviceAnalyticsContentUseCase,
    private val resetCurrentDeviceSelectedAnalyticsUseCase: ResetCurrentDeviceSelectedAnalyticsUseCase,
    private val removeAnalyticsItemUseCase: RemoveAnalyticsItemUseCase,
    private val removeAnalyticsItemsBeforeUseCase: RemoveAnalyticsItemsBeforeUseCase,
) : ViewModel() {

    private val _screenState = MutableStateFlow<AnalyticsScreenUiState>(
        AnalyticsScreenUiState(
            autoScroll = false,
            invertList = false,
        )
    )
    val screenState = _screenState.asStateFlow()
    val itemsState: StateFlow<AnalyticsStateUiModel> = analyticsSelectorDelegate.deviceAnalyticss

    private val _selectedItem = MutableStateFlow<AnalyticsRowUiModel?>(null)
    val selectedItem = _selectedItem.asStateFlow()

    val content: StateFlow<AnalyticsContentStateUiModel> =
        observeCurrentDeviceAnalyticsContentUseCase()
            .map {
                if (it.isEmpty()) {
                    AnalyticsContentStateUiModel.Empty
                } else {
                    AnalyticsContentStateUiModel.WithContent(
                        rows = it.map { item ->
                            item.mapToUi()
                        },
                    )
                }
            }
            .flowOn(dispatcherProvider.viewModel)
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AnalyticsContentStateUiModel.Loading,
            )



    fun onVisible() {
        // no op
    }

    fun onNotVisible() {
        // no op
    }

    fun onAnalyticsSelected(selected: DeviceAnalyticsUiModel) {
        analyticsSelectorDelegate.onAnalyticsSelected(selected)
    }

    fun onResetClicked() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            resetCurrentDeviceSelectedAnalyticsUseCase()
        }
    }

    fun onAction(action: AnalyticsAction) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            when(action) {
                AnalyticsAction.ClosePanel -> {
                    _selectedItem.update {
                        null
                    }
                }
                is AnalyticsAction.OnClick -> {
                    _selectedItem.update {
                        if (it == action.item) {
                            null
                        } else {
                            action.item
                        }
                    }
                }
                is AnalyticsAction.Remove -> removeAnalyticsItemUseCase(action.item.id)
                is AnalyticsAction.RemoveLinesAbove -> removeAnalyticsItemsBeforeUseCase(action.item.id)
                is AnalyticsAction.ToggleAutoScroll -> _screenState.update { it.copy(autoScroll = !it.autoScroll) }
                is AnalyticsAction.InvertList -> _screenState.update { it.copy(invertList = action.value) }
                is AnalyticsAction.ClearOldSession -> {
                    // TODO
                }
            }
        }
    }

}
