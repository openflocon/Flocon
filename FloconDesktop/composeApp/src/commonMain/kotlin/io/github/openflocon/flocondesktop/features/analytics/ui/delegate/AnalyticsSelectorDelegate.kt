package io.github.openflocon.flocondesktop.features.analytics.ui.delegate

import com.florent37.flocondesktop.common.coroutines.closeable.CloseableDelegate
import com.florent37.flocondesktop.common.coroutines.closeable.CloseableScoped
import com.florent37.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import com.florent37.flocondesktop.features.analytics.domain.ObserveCurrentDeviceSelectedAnalyticsUseCase
import com.florent37.flocondesktop.features.analytics.domain.ObserveDeviceAnalyticsUseCase
import com.florent37.flocondesktop.features.analytics.domain.SelectCurrentDeviceAnalyticsUseCase
import com.florent37.flocondesktop.features.analytics.domain.model.AnalyticsIdentifierDomainModel
import com.florent37.flocondesktop.features.analytics.ui.model.AnalyticsStateUiModel
import com.florent37.flocondesktop.features.analytics.ui.model.DeviceAnalyticsUiModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AnalyticsSelectorDelegate(
    observeDeviceAnalyticsUseCase: ObserveDeviceAnalyticsUseCase,
    observeCurrentDeviceSelectedAnalyticsUseCase: ObserveCurrentDeviceSelectedAnalyticsUseCase,
    private val closeableDelegate: CloseableDelegate,
    private val dispatcherProvider: DispatcherProvider,
    private val selectCurrentDeviceAnalyticsUseCase: SelectCurrentDeviceAnalyticsUseCase,
) : CloseableScoped by closeableDelegate {

    val deviceAnalyticss: StateFlow<AnalyticsStateUiModel> =
        combine(
            observeDeviceAnalyticsUseCase(),
            observeCurrentDeviceSelectedAnalyticsUseCase(),
        ) { analyticss, selected ->
            if (analyticss.isEmpty()) {
                AnalyticsStateUiModel.Empty
            } else {
                AnalyticsStateUiModel.WithContent(
                    analytics = analyticss.map { toUi(it) },
                    selected =
                    toUi(
                        selected ?: run {
                            analyticss.first().also {
                                selectCurrentDeviceAnalyticsUseCase(it.id)
                            }
                        },
                    ),
                )
            }
        }.flowOn(dispatcherProvider.viewModel)
            .stateIn(
                coroutineScope,
                SharingStarted.Companion.WhileSubscribed(5_000),
                AnalyticsStateUiModel.Loading,
            )

    fun toUi(analytics: AnalyticsIdentifierDomainModel) = DeviceAnalyticsUiModel(
        id = analytics.id,
        name = analytics.name,
    )

    fun onAnalyticsSelected(analytics: DeviceAnalyticsUiModel) {
        coroutineScope.launch(dispatcherProvider.viewModel) {
            selectCurrentDeviceAnalyticsUseCase(analytics.id)
        }
    }
}
