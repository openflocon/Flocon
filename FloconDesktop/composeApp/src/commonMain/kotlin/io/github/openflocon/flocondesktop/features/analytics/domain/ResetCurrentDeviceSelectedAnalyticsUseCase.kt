package io.github.openflocon.flocondesktop.features.analytics.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.features.analytics.domain.repository.AnalyticsRepository

class ResetCurrentDeviceSelectedAnalyticsUseCase(
    private val analyticsRepository: AnalyticsRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
    private val getCurrentDeviceSelectedAnalyticsUseCase: GetCurrentDeviceSelectedAnalyticsUseCase,
) {
    suspend operator fun invoke() {
        val deviceId = getCurrentDeviceIdUseCase() ?: return
        val analyticsId = getCurrentDeviceSelectedAnalyticsUseCase() ?: return
        analyticsRepository.deleteAnalytics(deviceId = deviceId, analyticsId = analyticsId)
    }
}
