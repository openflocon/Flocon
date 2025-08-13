package io.github.openflocon.flocondesktop.features.analytics.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.analytics.usecase.ObserveCurrentDeviceAnalyticsContentUseCase
import io.github.openflocon.domain.analytics.usecase.ResetCurrentDeviceSelectedAnalyticsUseCase
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.flocondesktop.common.ui.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.features.analytics.ui.delegate.AnalyticsSelectorDelegate
import io.github.openflocon.flocondesktop.features.analytics.ui.model.AnalyticsContentStateUiModel
import io.github.openflocon.flocondesktop.features.analytics.ui.model.AnalyticsRowUiModel
import io.github.openflocon.flocondesktop.features.analytics.ui.model.AnalyticsStateUiModel
import io.github.openflocon.flocondesktop.features.analytics.ui.model.DeviceAnalyticsUiModel
import io.github.openflocon.flocondesktop.features.network.ui.mapper.formatTimestamp
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
    private val observeCurrentDeviceAnalyticsContentUseCase: ObserveCurrentDeviceAnalyticsContentUseCase,
    private val resetCurrentDeviceSelectedAnalyticsUseCase: ResetCurrentDeviceSelectedAnalyticsUseCase,
) : ViewModel() {

    companion object {
        const val MAX_PROPERTIES_TO_SHOW = 10
    }

    val deviceAnalytics: StateFlow<AnalyticsStateUiModel> = analyticsSelectorDelegate.deviceAnalyticss

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
                            AnalyticsRowUiModel(
                                dateFormatted = formatTimestamp(item.createdAt),
                                eventName = item.eventName,
                                properties = item.properties.map {
                                    AnalyticsRowUiModel.PropertyUiModel(
                                        name = it.name,
                                        value = it.value,
                                    )
                                }.take(MAX_PROPERTIES_TO_SHOW),
                                hasMoreProperties = item.properties.size > MAX_PROPERTIES_TO_SHOW,
                            )
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

    fun onClickItem(item: AnalyticsRowUiModel?) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            _selectedItem.update {
                if (it == item) {
                    null
                } else {
                    item
                }
            }
        }
    }
}
