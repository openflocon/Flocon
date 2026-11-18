package io.github.openflocon.domain.analytics.usecase

import io.github.openflocon.domain.analytics.models.AnalyticsIdentifierDomainModel
import io.github.openflocon.domain.analytics.repository.AnalyticsRepository
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
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
