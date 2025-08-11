package io.github.openflocon.flocondesktop.features.analytics.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.models.AnalyticsTableId
import io.github.openflocon.flocondesktop.features.analytics.domain.repository.AnalyticsRepository

class SelectCurrentDeviceAnalyticsUseCase(
    private val analyticsRepository: AnalyticsRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke(analyticsTableId: AnalyticsTableId) {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return

        analyticsRepository.selectDeviceAnalytics(
            deviceIdAndPackageName = current,
            analyticsTableId = analyticsTableId,
        )
    }
}
