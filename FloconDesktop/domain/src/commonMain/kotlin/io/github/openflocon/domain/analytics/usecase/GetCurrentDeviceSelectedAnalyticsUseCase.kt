package io.github.openflocon.domain.analytics.usecase

import io.github.openflocon.domain.analytics.models.AnalyticsIdentifierDomainModel
import kotlinx.coroutines.flow.firstOrNull

class GetCurrentDeviceSelectedAnalyticsUseCase(
    private val observeCurrentDeviceSelectedAnalyticsUseCase: ObserveCurrentDeviceSelectedAnalyticsUseCase,
) {
    suspend operator fun invoke(): AnalyticsIdentifierDomainModel? = observeCurrentDeviceSelectedAnalyticsUseCase().firstOrNull()
}
