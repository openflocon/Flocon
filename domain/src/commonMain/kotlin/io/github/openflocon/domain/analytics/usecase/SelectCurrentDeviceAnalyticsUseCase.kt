package io.github.openflocon.domain.analytics.usecase

import io.github.openflocon.domain.analytics.models.AnalyticsTableId
import io.github.openflocon.domain.analytics.repository.AnalyticsRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase

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
