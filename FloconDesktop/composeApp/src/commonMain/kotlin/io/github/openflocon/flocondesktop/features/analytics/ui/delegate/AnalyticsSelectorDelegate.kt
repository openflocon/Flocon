package io.github.openflocon.flocondesktop.features.analytics.ui.delegate

import io.github.openflocon.domain.common.coroutines.closeable.CloseableDelegate
import io.github.openflocon.domain.common.coroutines.closeable.CloseableScoped
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.analytics.usecase.ObserveCurrentDeviceSelectedAnalyticsUseCase
import io.github.openflocon.domain.analytics.usecase.ObserveDeviceAnalyticsUseCase
import io.github.openflocon.domain.analytics.usecase.SelectCurrentDeviceAnalyticsUseCase
import io.github.openflocon.domain.analytics.models.AnalyticsIdentifierDomainModel
import io.github.openflocon.flocondesktop.features.analytics.ui.model.AnalyticsStateUiModel
import io.github.openflocon.flocondesktop.features.analytics.ui.model.DeviceAnalyticsUiModel
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
