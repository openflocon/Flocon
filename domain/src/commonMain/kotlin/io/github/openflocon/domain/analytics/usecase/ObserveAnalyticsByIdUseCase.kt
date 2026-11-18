package io.github.openflocon.domain.analytics.usecase

import io.github.openflocon.domain.analytics.models.AnalyticsItemDomainModel
import io.github.openflocon.domain.analytics.repository.AnalyticsRepository
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveAnalyticsByIdUseCase(
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val analyticsRepository: AnalyticsRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(id: String): Flow<AnalyticsItemDomainModel?> = observeCurrentDeviceIdAndPackageNameUseCase()
        .flatMapLatest { deviceIdAndPackageName ->
            if (deviceIdAndPackageName == null) {
                flowOf(null)
            } else {
                analyticsRepository.observeAnalyticsById(
                    deviceIdAndPackageName = deviceIdAndPackageName,
                    id = id,
                )
            }
        }
}
