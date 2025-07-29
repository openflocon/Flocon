package com.florent37.flocondesktop.features.analytics.domain

import com.florent37.flocondesktop.core.domain.device.ObserveCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.analytics.domain.model.AnalyticsItemDomainModel
import com.florent37.flocondesktop.features.analytics.domain.repository.AnalyticsRepository
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
