package io.github.openflocon.flocondesktop.features.analytics.domain

import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.models.AnalyticsIdentifierDomainModel
import io.github.openflocon.flocondesktop.features.analytics.domain.repository.AnalyticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveCurrentDeviceSelectedAnalyticsUseCase(
    private val analyticsRepository: AnalyticsRepository,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(): Flow<AnalyticsIdentifierDomainModel?> = observeCurrentDeviceIdAndPackageNameUseCase()
        .flatMapLatest { model ->
            if (model == null) {
                flowOf(null)
            } else {
                analyticsRepository.observeSelectedDeviceAnalytics(deviceIdAndPackageName = model)
            }
        }
}
