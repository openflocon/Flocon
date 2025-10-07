package io.github.openflocon.domain.analytics

import io.github.openflocon.domain.analytics.usecase.ExportAnalyticsToCsvUseCase
import io.github.openflocon.domain.analytics.usecase.GetCurrentDeviceAnalyticsContentUseCase
import io.github.openflocon.domain.analytics.usecase.GetCurrentDeviceSelectedAnalyticsUseCase
import io.github.openflocon.domain.analytics.usecase.ObserveAnalyticsByIdUseCase
import io.github.openflocon.domain.analytics.usecase.ObserveCurrentDeviceAnalyticsContentUseCase
import io.github.openflocon.domain.analytics.usecase.ObserveCurrentDeviceSelectedAnalyticsUseCase
import io.github.openflocon.domain.analytics.usecase.ObserveDeviceAnalyticsUseCase
import io.github.openflocon.domain.analytics.usecase.RemoveAnalyticsItemUseCase
import io.github.openflocon.domain.analytics.usecase.RemoveAnalyticsItemsBeforeUseCase
import io.github.openflocon.domain.analytics.usecase.RemoveOldSessionsAnalyticsUseCase
import io.github.openflocon.domain.analytics.usecase.ResetCurrentDeviceSelectedAnalyticsUseCase
import io.github.openflocon.domain.analytics.usecase.SelectCurrentDeviceAnalyticsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val analyticsModule = module {
    factoryOf(::GetCurrentDeviceSelectedAnalyticsUseCase)
    factoryOf(::ObserveCurrentDeviceAnalyticsContentUseCase)
    factoryOf(::GetCurrentDeviceAnalyticsContentUseCase)
    factoryOf(::ObserveCurrentDeviceSelectedAnalyticsUseCase)
    factoryOf(::ObserveDeviceAnalyticsUseCase)
    factoryOf(::ResetCurrentDeviceSelectedAnalyticsUseCase)
    factoryOf(::SelectCurrentDeviceAnalyticsUseCase)
    factoryOf(::RemoveAnalyticsItemUseCase)
    factoryOf(::RemoveAnalyticsItemsBeforeUseCase)
    factoryOf(::RemoveOldSessionsAnalyticsUseCase)
    factoryOf(::ExportAnalyticsToCsvUseCase)
    factoryOf(::ObserveAnalyticsByIdUseCase)
}
