package io.github.openflocon.domain.analytics.usecase

import io.github.openflocon.domain.analytics.models.AnalyticsItemDomainModel
import io.github.openflocon.domain.analytics.repository.AnalyticsRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import kotlinx.coroutines.flow.firstOrNull

class GetCurrentDeviceAnalyticsContentUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val analyticsRepository: AnalyticsRepository,
) {
    suspend operator fun invoke(
        filter: String?,
    ): List<AnalyticsItemDomainModel> =
        getCurrentDeviceIdAndPackageNameUseCase()?.let { deviceIdAndPackageName ->
            analyticsRepository.observeSelectedDeviceAnalytics(
                deviceIdAndPackageName = deviceIdAndPackageName
            ).firstOrNull()?.let { selectedAnalytics ->
                analyticsRepository.getAnalytics(
                    deviceIdAndPackageName = deviceIdAndPackageName,
                    analyticsTableId = selectedAnalytics.id,
                    filter = filter
                )
            }
        } ?: emptyList()
}
