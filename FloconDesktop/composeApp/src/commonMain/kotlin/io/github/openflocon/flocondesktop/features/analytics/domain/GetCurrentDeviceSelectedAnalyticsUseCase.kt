package io.github.openflocon.flocondesktop.features.analytics.domain

import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsIdentifierDomainModel
import kotlinx.coroutines.flow.firstOrNull

class GetCurrentDeviceSelectedAnalyticsUseCase(
    private val observeCurrentDeviceSelectedAnalyticsUseCase: ObserveCurrentDeviceSelectedAnalyticsUseCase,
) {
    suspend operator fun invoke(): AnalyticsIdentifierDomainModel? = observeCurrentDeviceSelectedAnalyticsUseCase().firstOrNull()
}
