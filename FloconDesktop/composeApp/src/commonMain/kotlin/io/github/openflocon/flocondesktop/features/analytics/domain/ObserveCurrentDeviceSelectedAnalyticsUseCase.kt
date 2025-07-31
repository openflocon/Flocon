package io.github.openflocon.flocondesktop.features.analytics.domain

import com.florent37.flocondesktop.core.domain.device.ObserveCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.analytics.domain.model.AnalyticsIdentifierDomainModel
import com.florent37.flocondesktop.features.analytics.domain.repository.AnalyticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveCurrentDeviceSelectedAnalyticsUseCase(
    private val analyticsRepository: AnalyticsRepository,
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
) {
    operator fun invoke(): Flow<AnalyticsIdentifierDomainModel?> = observeCurrentDeviceIdUseCase().flatMapLatest { deviceId ->
        if (deviceId == null) {
            flowOf(null)
        } else {
            analyticsRepository.observeSelectedDeviceAnalytics(deviceId = deviceId)
        }
    }
}
