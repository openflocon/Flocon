package io.github.openflocon.domain.analytics.usecase

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceAppUseCase
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdUseCase
import io.github.openflocon.domain.analytics.repository.AnalyticsRepository
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel

class ResetCurrentDeviceSelectedAnalyticsUseCase(
    private val analyticsRepository: AnalyticsRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
    private val getCurrentPackageNameUseCase: GetCurrentDeviceAppUseCase,
    private val getCurrentDeviceSelectedAnalyticsUseCase: GetCurrentDeviceSelectedAnalyticsUseCase,
) {
    suspend operator fun invoke() {
        val deviceId = getCurrentDeviceIdUseCase() ?: return
        val packageName = getCurrentPackageNameUseCase()?.packageName ?: return
        val analyticsId = getCurrentDeviceSelectedAnalyticsUseCase() ?: return

        analyticsRepository.deleteAnalytics(
            deviceIdAndPackageName = DeviceIdAndPackageNameDomainModel(
                deviceId = deviceId,
                packageName = packageName,
            ),
            analyticsId = analyticsId,
        )
    }
}
