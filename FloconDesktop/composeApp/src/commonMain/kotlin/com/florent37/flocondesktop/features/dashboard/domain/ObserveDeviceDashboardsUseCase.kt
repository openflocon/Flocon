package com.florent37.flocondesktop.features.dashboard.domain

import com.florent37.flocondesktop.core.domain.device.ObserveCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.dashboard.domain.model.DashboardId
import com.florent37.flocondesktop.features.dashboard.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveDeviceDashboardsUseCase(
    private val dashboardRepository: DashboardRepository,
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
) {
    operator fun invoke(): Flow<List<DashboardId>> = observeCurrentDeviceIdUseCase().flatMapLatest { deviceId ->
        if (deviceId == null) {
            flowOf(emptyList())
        } else {
            dashboardRepository.observeDeviceDashboards(deviceId = deviceId)
        }
    }
}
