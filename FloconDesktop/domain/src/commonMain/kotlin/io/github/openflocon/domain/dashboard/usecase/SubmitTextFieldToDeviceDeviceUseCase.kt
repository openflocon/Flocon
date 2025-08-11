package io.github.openflocon.domain.dashboard.usecase

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.dashboard.repository.DashboardRepository

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
