package io.github.openflocon.domain.dashboard.usecase

class DeleteCurrentDeviceSelectedDashboardUseCase(
    private val deleteDashboardUseCase: DeleteDashboardUseCase,
    private val getCurrentDeviceSelectedDashboardUseCase: GetCurrentDeviceSelectedDashboardUseCase,
) {
    suspend operator fun invoke() {
        getCurrentDeviceSelectedDashboardUseCase()?.let {
            deleteDashboardUseCase(it)
        }
    }
}
