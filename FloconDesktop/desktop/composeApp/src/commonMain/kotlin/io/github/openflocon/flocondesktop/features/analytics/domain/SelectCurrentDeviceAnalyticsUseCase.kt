package io.github.openflocon.flocondesktop.features.analytics.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsTableId
import io.github.openflocon.flocondesktop.features.analytics.domain.repository.AnalyticsRepository

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
