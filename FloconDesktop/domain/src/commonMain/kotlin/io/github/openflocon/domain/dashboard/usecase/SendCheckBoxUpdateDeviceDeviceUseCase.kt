package io.github.openflocon.domain.dashboard.usecase

import io.github.openflocon.domain.dashboard.repository.DashboardRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase

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
