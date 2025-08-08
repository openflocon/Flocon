package io.github.openflocon.flocondesktop.features.dashboard.domain

import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.flocondesktop.features.dashboard.domain.model.DashboardId
import io.github.openflocon.flocondesktop.features.dashboard.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveDeviceDashboardsUseCase(
    private val dashboardRepository: DashboardRepository,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(): Flow<List<DashboardId>> = observeCurrentDeviceIdAndPackageNameUseCase().flatMapLatest { model ->
        if (model == null) {
            flowOf(emptyList())
        } else {
            dashboardRepository.observeDeviceDashboards(deviceIdAndPackageName = model)
        }
    }
}
