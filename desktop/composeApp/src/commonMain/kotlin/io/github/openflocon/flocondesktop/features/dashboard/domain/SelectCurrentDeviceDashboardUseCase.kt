package io.github.openflocon.flocondesktop.features.dashboard.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.features.dashboard.domain.model.DashboardId
import io.github.openflocon.flocondesktop.features.dashboard.domain.repository.DashboardRepository

class SelectCurrentDeviceDashboardUseCase(
    private val dashboardRepository: DashboardRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
) {
    suspend operator fun invoke(dashboardId: DashboardId) {
        val currentDevice = getCurrentDeviceIdUseCase() ?: return
        dashboardRepository.selectDeviceDashboard(
            deviceId = currentDevice,
            dashboardId = dashboardId,
        )
    }
}
