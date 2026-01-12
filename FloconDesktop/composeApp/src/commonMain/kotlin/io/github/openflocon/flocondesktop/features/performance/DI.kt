package io.github.openflocon.flocondesktop.features.performance

import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val performanceModule = module {
    viewModelOf(::PerformanceViewModel)
}
