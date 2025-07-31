package io.github.openflocon.flocondesktop.features.dashboard.domain

import com.florent37.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.dashboard.domain.repository.DashboardRepository

class SendClickEventToDeviceDeviceUseCase(
    private val dashboardRepository: DashboardRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
    private val getCurrentDeviceSelectedDashboardUseCase: GetCurrentDeviceSelectedDashboardUseCase,
) {
    suspend operator fun invoke(buttonId: String) {
        val deviceId = getCurrentDeviceIdUseCase() ?: return
        val currentDashboard = getCurrentDeviceSelectedDashboardUseCase() ?: return
        dashboardRepository.sendClickEvent(
            deviceId = deviceId,
            dashboardId = currentDashboard,
            buttonId = buttonId,
        )
    }
}
