package io.github.openflocon.flocondesktop.features.dashboard.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.flocondesktop.features.dashboard.domain.model.DashboardId
import io.github.openflocon.flocondesktop.features.dashboard.domain.repository.DashboardRepository

class SelectCurrentDeviceDashboardUseCase(
    private val dashboardRepository: DashboardRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke(dashboardId: DashboardId) {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return

        dashboardRepository.selectDeviceDashboard(
            deviceIdAndPackageName = current,
            dashboardId = dashboardId,
        )
    }
}
