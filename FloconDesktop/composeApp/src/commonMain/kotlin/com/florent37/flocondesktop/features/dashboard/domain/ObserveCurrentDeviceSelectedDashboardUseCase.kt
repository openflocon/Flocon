package com.florent37.flocondesktop.features.dashboard.domain

import com.florent37.flocondesktop.core.domain.device.ObserveCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.dashboard.domain.model.DashboardId
import com.florent37.flocondesktop.features.dashboard.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveCurrentDeviceSelectedDashboardUseCase(
    private val dashboardRepository: DashboardRepository,
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
) {
    operator fun invoke(): Flow<DashboardId?> = observeCurrentDeviceIdUseCase().flatMapLatest { deviceId ->
        if (deviceId == null) {
            flowOf(null)
        } else {
            dashboardRepository.observeSelectedDeviceDashboard(deviceId = deviceId)
        }
    }
}
