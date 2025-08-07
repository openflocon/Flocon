package io.github.openflocon.flocondesktop.features.dashboard.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.flocondesktop.features.dashboard.domain.repository.DashboardRepository

class SendCheckBoxUpdateDeviceDeviceUseCase(
    private val dashboardRepository: DashboardRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val getCurrentDeviceSelectedDashboardUseCase: GetCurrentDeviceSelectedDashboardUseCase,
) {
    suspend operator fun invoke(checkBoxId: String, value: Boolean) {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return
        val currentDashboard = getCurrentDeviceSelectedDashboardUseCase() ?: return

        dashboardRepository.sendUpdateCheckBoxEvent(
            deviceIdAndPackageName = current,
            dashboardId = currentDashboard,
            checkBoxId = checkBoxId,
            value = value,
        )
    }
}
