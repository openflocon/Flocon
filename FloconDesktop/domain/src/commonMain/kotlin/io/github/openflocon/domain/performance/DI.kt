package io.github.openflocon.domain.performance

import io.github.openflocon.domain.performance.usecase.*
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val performanceDomainModule = module {
    factoryOf(::FetchPerformanceMetricsUseCase)
    factoryOf(::GetRamUsageUseCase)
    factoryOf(::GetBatteryLevelUseCase)
    factoryOf(::CapturePerformanceScreenshotUseCase)
    factoryOf(::GetFpsUseCase)
    factoryOf(::GetAdbDevicesUseCase)
    factoryOf(::GetDeviceRefreshRateUseCase)
}
