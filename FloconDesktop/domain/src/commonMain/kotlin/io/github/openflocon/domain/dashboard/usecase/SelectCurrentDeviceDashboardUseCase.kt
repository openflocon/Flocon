package io.github.openflocon.domain.dashboard.usecase

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.dashboard.models.DashboardId
import io.github.openflocon.domain.dashboard.repository.DashboardRepository

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
