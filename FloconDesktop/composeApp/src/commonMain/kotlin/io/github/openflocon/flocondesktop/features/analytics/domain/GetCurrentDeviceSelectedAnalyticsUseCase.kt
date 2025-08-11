package io.github.openflocon.flocondesktop.features.analytics.domain

import com.flocon.library.domain.models.AnalyticsIdentifierDomainModel
import kotlinx.coroutines.flow.firstOrNull

class GetCurrentDeviceSelectedAnalyticsUseCase(
    private val observeCurrentDeviceSelectedAnalyticsUseCase: ObserveCurrentDeviceSelectedAnalyticsUseCase,
) {
    suspend operator fun invoke(): AnalyticsIdentifierDomainModel? = observeCurrentDeviceSelectedAnalyticsUseCase().firstOrNull()
}
