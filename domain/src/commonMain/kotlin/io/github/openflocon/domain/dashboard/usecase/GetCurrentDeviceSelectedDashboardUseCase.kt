package io.github.openflocon.domain.dashboard.usecase

import io.github.openflocon.domain.dashboard.models.DashboardId
import kotlinx.coroutines.flow.firstOrNull

class GetCurrentDeviceSelectedDashboardUseCase(
    private val observeCurrentDeviceSelectedDashboardUseCase: ObserveCurrentDeviceSelectedDashboardUseCase,
) {
    suspend operator fun invoke(): DashboardId? = observeCurrentDeviceSelectedDashboardUseCase().firstOrNull()
}
