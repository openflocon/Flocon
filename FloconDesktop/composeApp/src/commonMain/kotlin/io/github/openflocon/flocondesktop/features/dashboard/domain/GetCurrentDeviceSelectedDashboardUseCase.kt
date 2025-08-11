package io.github.openflocon.flocondesktop.features.dashboard.domain

import com.flocon.library.domain.models.DashboardId
import kotlinx.coroutines.flow.firstOrNull

class GetCurrentDeviceSelectedDashboardUseCase(
    private val observeCurrentDeviceSelectedDashboardUseCase: ObserveCurrentDeviceSelectedDashboardUseCase,
) {
    suspend operator fun invoke(): DashboardId? = observeCurrentDeviceSelectedDashboardUseCase().firstOrNull()
}
