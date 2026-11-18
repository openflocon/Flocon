package io.github.openflocon.domain.analytics.usecase

import io.github.openflocon.domain.analytics.repository.AnalyticsRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase

class ResetCurrentDeviceSelectedAnalyticsUseCase(
    private val analyticsRepository: AnalyticsRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val getCurrentDeviceSelectedAnalyticsUseCase: GetCurrentDeviceSelectedAnalyticsUseCase,
) {
    suspend operator fun invoke() {
        val deviceAndApp = getCurrentDeviceIdAndPackageNameUseCase() ?: return
        val analyticsId = getCurrentDeviceSelectedAnalyticsUseCase() ?: return

        analyticsRepository.deleteAnalytics(
            deviceIdAndPackageName = deviceAndApp,
            analyticsId = analyticsId,
        )
    }
}
