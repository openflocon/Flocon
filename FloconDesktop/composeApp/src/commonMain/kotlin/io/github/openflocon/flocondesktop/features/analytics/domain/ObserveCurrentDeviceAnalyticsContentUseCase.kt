package io.github.openflocon.flocondesktop.features.analytics.domain

import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceIdAndPackageNameUseCase
import com.flocon.library.domain.models.AnalyticsItemDomainModel
import io.github.openflocon.flocondesktop.features.analytics.domain.repository.AnalyticsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveCurrentDeviceAnalyticsContentUseCase(
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val analyticsRepository: AnalyticsRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<AnalyticsItemDomainModel>> = observeCurrentDeviceIdAndPackageNameUseCase()
        .flatMapLatest { deviceIdAndPackageName ->
            if (deviceIdAndPackageName == null) {
                flowOf(emptyList())
            } else {
                analyticsRepository
                    .observeSelectedDeviceAnalytics(deviceIdAndPackageName = deviceIdAndPackageName)
                    .flatMapLatest { selectedAnalytics ->
                        if (selectedAnalytics == null) {
                            flowOf(emptyList())
                        } else {
                            analyticsRepository.observeAnalytics(
                                deviceIdAndPackageName = deviceIdAndPackageName,
                                analyticsTableId = selectedAnalytics.id,
                            )
                        }
                    }
            }
        }
}
