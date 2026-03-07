package io.github.openflocon.domain.dashboard.usecase

import io.github.openflocon.domain.dashboard.models.DashboardArrangementDomainModel
import io.github.openflocon.domain.dashboard.repository.DashboardRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase

class SelectDashboardArrangementUseCase(
    private val dashboardRepository: DashboardRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val getCurrentDeviceSelectedDashboardUseCase: GetCurrentDeviceSelectedDashboardUseCase,
) {
    suspend operator fun invoke(arrangement: DashboardArrangementDomainModel) {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return
        val currentSelectedDashboard = getCurrentDeviceSelectedDashboardUseCase() ?: return

        dashboardRepository.selectDashboardArrangement(
            dashboardId = currentSelectedDashboard,
            deviceIdAndPackageName = current,
            arrangement = arrangement,
        )
    }
}
