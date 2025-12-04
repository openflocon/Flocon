package io.github.openflocon.flocondesktop.features.analytics

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.error_exporting_csv
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
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsDetailUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsRowUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsScreenUiState
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsStateUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.DeviceAnalyticsUiModel
import io.github.openflocon.library.designsystem.common.asState
import io.github.openflocon.navigation.MainFloconNavigationState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class AnalyticsViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val analyticsSelectorDelegate: AnalyticsSelectorDelegate,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    observeCurrentDeviceAnalyticsContentUseCase: ObserveCurrentDeviceAnalyticsContentUseCase,
    private val resetCurrentDeviceSelectedAnalyticsUseCase: ResetCurrentDeviceSelectedAnalyticsUseCase,
    private val removeAnalyticsItemUseCase: RemoveAnalyticsItemUseCase,
    private val removeAnalyticsItemsBeforeUseCase: RemoveAnalyticsItemsBeforeUseCase,
    private val removeOldSessionsAnalyticsUseCase: RemoveOldSessionsAnalyticsUseCase,
    private val exportAnalyticsToCsv: ExportAnalyticsToCsvUseCase,
    private val observeAnalyticsByIdUseCase: ObserveAnalyticsByIdUseCase,
    private val navigationState: MainFloconNavigationState
) : ViewModel() {

    private val _filterText = mutableStateOf("")
    val filterText: State<String> = _filterText.asState()

    private val _screenState = MutableStateFlow(
        AnalyticsScreenUiState(
            autoScroll = false,
            invertList = false,
        )
    )
    val screenState = _screenState.asStateFlow()
    val itemsState: StateFlow<AnalyticsStateUiModel> = analyticsSelectorDelegate.deviceAnalyticss

    private val selectedItemId = MutableStateFlow<String?>(null)
    val selectedItem: StateFlow<AnalyticsDetailUiModel?> = combines(
        selectedItemId,
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

    val rows: Flow<PagingData<AnalyticsRowUiModel>> =
        observeCurrentDeviceIdAndPackageNameUseCase().flatMapLatest { device ->
            snapshotFlow { _filterText.value }
                .flatMapLatest { filter ->
                    observeCurrentDeviceAnalyticsContentUseCase(
                        filter = filter.takeIf { it.isNotBlank() },
                    ).map { pagingData ->
                        pagingData.map {
                            it.mapToUi(
                                deviceIdAndPackageName = device
                            )
                        }
                    }
                }
        }.flowOn(dispatcherProvider.viewModel)
            .cachedIn(
                viewModelScope,
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
                    selectedItemId.update {
                        null
                    }
                }

                is AnalyticsAction.OnClick -> {
                    navigationState.navigate(AnalyticsRoutes.Detail(action.item.id))
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
            exportAnalyticsToCsv(
                filter = _filterText.value.takeIf { it.isNotBlank() }
            ).fold(
                doOnFailure = {
                    feedbackDisplayer.displayMessage(
                        getString(Res.string.error_exporting_csv)
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
