package io.github.openflocon.flocondesktop.features.dashboard.domain

import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceIdAndPackageNameUseCase
import com.flocon.library.domain.models.DashboardDomainModel
import io.github.openflocon.flocondesktop.features.dashboard.domain.repository.DashboardRepository
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
