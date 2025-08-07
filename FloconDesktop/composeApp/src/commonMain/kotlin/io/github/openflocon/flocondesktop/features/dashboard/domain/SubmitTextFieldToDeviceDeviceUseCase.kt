package io.github.openflocon.flocondesktop.features.dashboard.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.flocondesktop.features.dashboard.domain.repository.DashboardRepository

class SubmitTextFieldToDeviceDeviceUseCase(
    private val dashboardRepository: DashboardRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val getCurrentDeviceSelectedDashboardUseCase: GetCurrentDeviceSelectedDashboardUseCase,
) {
    suspend operator fun invoke(textFieldId: String, value: String) {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return
        val currentDashboard = getCurrentDeviceSelectedDashboardUseCase() ?: return

        dashboardRepository.submitTextFieldEvent(
            deviceIdAndPackageName = current,
            dashboardId = currentDashboard,
            textFieldId = textFieldId,
            value = value,
        )
    }
}
