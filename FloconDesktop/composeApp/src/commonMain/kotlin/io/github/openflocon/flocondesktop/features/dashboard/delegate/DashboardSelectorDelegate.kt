package io.github.openflocon.flocondesktop.features.dashboard.delegate

import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.dashboard.models.DashboardId
import io.github.openflocon.domain.dashboard.usecase.ObserveCurrentDeviceSelectedDashboardUseCase
import io.github.openflocon.domain.dashboard.usecase.ObserveDeviceDashboardsUseCase
import io.github.openflocon.domain.dashboard.usecase.SelectCurrentDeviceDashboardUseCase
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableDelegate
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableScoped
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardsStateUiModel
import io.github.openflocon.flocondesktop.features.dashboard.model.DeviceDashboardUiModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardSelectorDelegate(
    observeDeviceDashboardUseCase: ObserveDeviceDashboardsUseCase,
    observeCurrentDeviceSelectedDashboardUseCase: ObserveCurrentDeviceSelectedDashboardUseCase,
    private val closeableDelegate: CloseableDelegate,
    private val dispatcherProvider: DispatcherProvider,
    private val selectCurrentDeviceDashboardUseCase: SelectCurrentDeviceDashboardUseCase,
) : CloseableScoped by closeableDelegate {

    val deviceDashboards: StateFlow<DashboardsStateUiModel> =
        combine(
            observeDeviceDashboardUseCase(),
            observeCurrentDeviceSelectedDashboardUseCase(),
        ) { dashboards, selected ->
            if (dashboards.isEmpty()) {
                DashboardsStateUiModel.Empty
            } else {
                val sortedDashboards = dashboards.sortedBy { it }

                DashboardsStateUiModel.WithContent(
                    dashboards = sortedDashboards.map { it.toUi() },
                    selected = selected?.toUi() ?: run {
                        sortedDashboards.first().let {
                            selectCurrentDeviceDashboardUseCase(it)
                            it.toUi()
                        }
                    },
                )
            }
        }.flowOn(dispatcherProvider.viewModel)
            .stateIn(
                coroutineScope,
                SharingStarted.WhileSubscribed(5_000),
                DashboardsStateUiModel.Loading,
            )

    fun DashboardId.toUi() = DeviceDashboardUiModel(
        id = this,
    )

    fun onDashboardSelected(dashboardId: DashboardId) {
        coroutineScope.launch(dispatcherProvider.viewModel) {
            selectCurrentDeviceDashboardUseCase(dashboardId)
        }
    }
}
