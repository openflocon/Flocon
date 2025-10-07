package io.github.openflocon.domain.analytics.usecase

import androidx.paging.PagingData
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.analytics.models.AnalyticsItemDomainModel
import io.github.openflocon.domain.analytics.repository.AnalyticsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveCurrentDeviceAnalyticsContentUseCase(
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val analyticsRepository: AnalyticsRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(
        filter: String?,
    ): Flow<PagingData<AnalyticsItemDomainModel>> = observeCurrentDeviceIdAndPackageNameUseCase()
        .flatMapLatest { deviceIdAndPackageName ->
            if (deviceIdAndPackageName == null) {
                flowOf(PagingData.empty())
            } else {
                analyticsRepository
                    .observeSelectedDeviceAnalytics(deviceIdAndPackageName = deviceIdAndPackageName)
                    .flatMapLatest { selectedAnalytics ->
                        if (selectedAnalytics == null) {
                            flowOf(PagingData.empty())
                        } else {
                            analyticsRepository.observeAnalytics(
                                deviceIdAndPackageName = deviceIdAndPackageName,
                                analyticsTableId = selectedAnalytics.id,
                                filter = filter,
                            )
                        }
                    }
            }
        }
}
