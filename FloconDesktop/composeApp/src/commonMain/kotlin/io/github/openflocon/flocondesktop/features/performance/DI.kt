package io.github.openflocon.flocondesktop.features.performance

import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val performanceModule = module {
    singleOf(::PerformanceMetricsRepository)
    viewModelOf(::PerformanceViewModel)
    viewModel { (event: MetricEventUiModel) -> PerformanceDetailViewModel(event, get()) }
}
