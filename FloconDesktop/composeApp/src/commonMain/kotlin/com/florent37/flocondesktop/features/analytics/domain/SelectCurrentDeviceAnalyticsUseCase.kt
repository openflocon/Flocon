package com.florent37.flocondesktop.features.analytics.domain

import com.florent37.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.analytics.domain.model.AnalyticsTableId
import com.florent37.flocondesktop.features.analytics.domain.repository.AnalyticsRepository

class SelectCurrentDeviceAnalyticsUseCase(
    private val analyticsRepository: AnalyticsRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
) {
    suspend operator fun invoke(analyticsTableId: AnalyticsTableId) {
        val currentDevice = getCurrentDeviceIdUseCase() ?: return
        analyticsRepository.selectDeviceAnalytics(
            deviceId = currentDevice,
            analyticsTableId = analyticsTableId,
        )
    }
}
