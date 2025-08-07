package io.github.openflocon.flocondesktop.features.dashboard.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.flocondesktop.features.dashboard.domain.repository.DashboardRepository

class SendClickEventToDeviceDeviceUseCase(
    private val dashboardRepository: DashboardRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val getCurrentDeviceSelectedDashboardUseCase: GetCurrentDeviceSelectedDashboardUseCase,
) {
    suspend operator fun invoke(buttonId: String) {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return
        val currentDashboard = getCurrentDeviceSelectedDashboardUseCase() ?: return

        dashboardRepository.sendClickEvent(
            deviceIdAndPackageName = current,
            dashboardId = currentDashboard,
            buttonId = buttonId,
        )
    }
}
