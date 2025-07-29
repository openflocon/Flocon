package com.florent37.flocondesktop.features.dashboard.domain

import com.florent37.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.dashboard.domain.repository.DashboardRepository

class SendCheckBoxUpdateDeviceDeviceUseCase(
    private val dashboardRepository: DashboardRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
    private val getCurrentDeviceSelectedDashboardUseCase: GetCurrentDeviceSelectedDashboardUseCase,
) {
    suspend operator fun invoke(checkBoxId: String, value: Boolean) {
        val deviceId = getCurrentDeviceIdUseCase() ?: return
        val currentDashboard = getCurrentDeviceSelectedDashboardUseCase() ?: return
        dashboardRepository.sendUpdateCheckBoxEvent(
            deviceId = deviceId,
            dashboardId = currentDashboard,
            checkBoxId = checkBoxId,
            value = value,
        )
    }
}
