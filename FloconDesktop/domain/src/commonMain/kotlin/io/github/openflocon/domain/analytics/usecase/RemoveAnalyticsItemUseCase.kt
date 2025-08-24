package io.github.openflocon.domain.analytics.usecase

import io.github.openflocon.domain.analytics.repository.AnalyticsRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.repository.NetworkRepository

class RemoveAnalyticsItemUseCase(
    private val analyticsRepository: AnalyticsRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke(analyticsItemId: String) {
        getCurrentDeviceIdAndPackageNameUseCase()?.let { deviceIdAndPackageName ->
            analyticsRepository.deleteAnalyticsItem(
                deviceIdAndPackageName = deviceIdAndPackageName,
                analyticsItemId = analyticsItemId
            )
        }
    }
}
