package io.github.openflocon.flocondesktop.features.analytics.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceAppUseCase
import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.features.analytics.domain.repository.AnalyticsRepository
import com.flocon.library.domain.models.DeviceIdAndPackageNameDomainModel

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
