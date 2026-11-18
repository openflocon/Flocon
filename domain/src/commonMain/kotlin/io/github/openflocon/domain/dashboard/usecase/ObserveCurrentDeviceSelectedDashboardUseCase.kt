package io.github.openflocon.domain.dashboard.usecase

import io.github.openflocon.domain.dashboard.models.DashboardId
import io.github.openflocon.domain.dashboard.repository.DashboardRepository
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveCurrentDeviceSelectedDashboardUseCase(
    private val dashboardRepository: DashboardRepository,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(): Flow<DashboardId?> = observeCurrentDeviceIdAndPackageNameUseCase().flatMapLatest { model ->
        if (model == null) {
            flowOf(null)
        } else {
            dashboardRepository.observeSelectedDeviceDashboard(deviceIdAndPackageName = model)
        }
    }
}
