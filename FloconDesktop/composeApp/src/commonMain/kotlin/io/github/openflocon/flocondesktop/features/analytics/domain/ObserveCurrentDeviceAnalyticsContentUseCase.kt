package io.github.openflocon.flocondesktop.features.analytics.domain

import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsItemDomainModel
import io.github.openflocon.flocondesktop.features.analytics.domain.repository.AnalyticsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveCurrentDeviceAnalyticsContentUseCase(
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
    private val analyticsRepository: AnalyticsRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<AnalyticsItemDomainModel>> = observeCurrentDeviceIdUseCase()
        .flatMapLatest { deviceId ->
            if (deviceId == null) {
                flowOf(emptyList())
            } else {
                analyticsRepository
                    .observeSelectedDeviceAnalytics(deviceId = deviceId)
                    .flatMapLatest { selectedAnalytics ->
                        if (selectedAnalytics == null) {
                            flowOf(emptyList())
                        } else {
                            analyticsRepository.observeAnalytics(
                                deviceId = deviceId,
                                analyticsTableId = selectedAnalytics.id,
                            )
                        }
                    }
            }
        }
}
