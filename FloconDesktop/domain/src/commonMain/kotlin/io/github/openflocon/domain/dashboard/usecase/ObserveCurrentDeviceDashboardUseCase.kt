package io.github.openflocon.domain.dashboard.usecase

import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.dashboard.models.DashboardDomainModel
import io.github.openflocon.domain.dashboard.repository.DashboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveCurrentDeviceDashboardUseCase(
    private val dashboardRepository: DashboardRepository,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(): Flow<DashboardDomainModel?> = observeCurrentDeviceIdAndPackageNameUseCase().flatMapLatest { model ->
        if (model == null) {
            flowOf(null)
        } else {
            dashboardRepository.observeSelectedDeviceDashboard(deviceIdAndPackageName = model)
                .flatMapLatest { dashboardId ->
                    if (dashboardId == null) {
                        flowOf(null)
                    } else {
                        dashboardRepository.observeDashboard(
                            deviceIdAndPackageName = model,
                            dashboardId = dashboardId,
                        )
                    }
                }
        }
    }
}
