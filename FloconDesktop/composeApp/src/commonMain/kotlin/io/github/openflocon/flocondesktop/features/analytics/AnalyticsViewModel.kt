package io.github.openflocon.flocondesktop.features.analytics

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.analytics.usecase.ExportAnalyticsToCsvUseCase
import io.github.openflocon.domain.analytics.usecase.ObserveAnalyticsByIdUseCase
import io.github.openflocon.domain.analytics.usecase.ObserveCurrentDeviceAnalyticsContentUseCase
import io.github.openflocon.domain.analytics.usecase.RemoveAnalyticsItemUseCase
import io.github.openflocon.domain.analytics.usecase.RemoveAnalyticsItemsBeforeUseCase
import io.github.openflocon.domain.analytics.usecase.RemoveOldSessionsAnalyticsUseCase
import io.github.openflocon.domain.analytics.usecase.ResetCurrentDeviceSelectedAnalyticsUseCase
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.combines
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.features.analytics.delegate.AnalyticsSelectorDelegate
import io.github.openflocon.flocondesktop.features.analytics.mapper.mapToDetailUi
import io.github.openflocon.flocondesktop.features.analytics.mapper.mapToUi
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsAction
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsContentStateUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsDetailUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsScreenUiState
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsStateUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.DeviceAnalyticsUiModel
import io.github.openflocon.library.designsystem.common.asState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AnalyticsViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val analyticsSelectorDelegate: AnalyticsSelectorDelegate,
    observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    observeCurrentDeviceAnalyticsContentUseCase: ObserveCurrentDeviceAnalyticsContentUseCase,
    private val resetCurrentDeviceSelectedAnalyticsUseCase: ResetCurrentDeviceSelectedAnalyticsUseCase,
    private val removeAnalyticsItemUseCase: RemoveAnalyticsItemUseCase,
    private val removeAnalyticsItemsBeforeUseCase: RemoveAnalyticsItemsBeforeUseCase,
    private val removeOldSessionsAnalyticsUseCase: RemoveOldSessionsAnalyticsUseCase,
    private val exportAnalyticsToCsv: ExportAnalyticsToCsvUseCase,
    private val observeAnalyticsByIdUseCase: ObserveAnalyticsByIdUseCase,
) : ViewModel() {

    private val _filterText = mutableStateOf("")
    val filterText : State<String> = _filterText.asState()

    private val _screenState = MutableStateFlow(
        AnalyticsScreenUiState(
            autoScroll = false,
            invertList = false,
        )
    )
    val screenState = _screenState.asStateFlow()
    val itemsState: StateFlow<AnalyticsStateUiModel> = analyticsSelectorDelegate.deviceAnalyticss

    private val _selectedItemId = MutableStateFlow<String?>(null)
    val selectedItem: StateFlow<AnalyticsDetailUiModel?> = combines(
        _selectedItemId,
        observeCurrentDeviceIdAndPackageNameUseCase(),
    ).flatMapLatest { (id, device) ->
        if (id == null)
            flowOf(null)
        else
            observeAnalyticsByIdUseCase(id)
                .map {
                    it?.mapToDetailUi(device)
                }
    }.flowOn(dispatcherProvider.viewModel)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val content: StateFlow<AnalyticsContentStateUiModel> = observeCurrentDeviceIdAndPackageNameUseCase().flatMapLatest { device ->
        snapshotFlow { _filterText.value }
            .flatMapLatest { filter ->
                observeCurrentDeviceAnalyticsContentUseCase(
                    filter = filter.takeIf { it.isNotBlank() },
                )
            }
        .mapLatest { items ->
            if (items.isEmpty()) {
                AnalyticsContentStateUiModel.Empty
            } else {
                AnalyticsContentStateUiModel.WithContent(
                    rows = items.map { item ->
                        item.mapToUi(
                            deviceIdAndPackageName = device
                        )
                    },
                )
            }
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

    fun onFilterTextChanged(value: String) {
        _filterText.value = value
    }

    fun onAction(action: AnalyticsAction) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            when (action) {
                AnalyticsAction.ClosePanel -> {
                    _selectedItemId.update {
                        null
                    }
                }

                is AnalyticsAction.OnClick -> {
                    val newId = action.item.id
                    _selectedItemId.update {
                        if (it == newId) {
                            null
                        } else {
                            newId
                        }
                    }
                }

                is AnalyticsAction.Remove -> removeAnalyticsItemUseCase(action.item.id)
                is AnalyticsAction.RemoveLinesAbove -> removeAnalyticsItemsBeforeUseCase(action.item.id)
                is AnalyticsAction.ToggleAutoScroll -> _screenState.update { it.copy(autoScroll = !it.autoScroll) }
                is AnalyticsAction.InvertList -> _screenState.update { it.copy(invertList = action.value) }
                is AnalyticsAction.ClearOldSession -> removeOldSessionsAnalyticsUseCase()
                is AnalyticsAction.ExportCsv -> onExportCsv()
            }
        }
    }

    private fun onExportCsv() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            exportAnalyticsToCsv().fold(
                doOnFailure = {
                    feedbackDisplayer.displayMessage(
                        "Error while exporting csv"
                    )
                },
                doOnSuccess = { path ->
                    feedbackDisplayer.displayMessage(
                        "Csv exported at $path"
                    )
                }
            )
        }
    }

}
