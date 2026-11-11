package io.github.openflocon.flocondesktop.telemetry

import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val telemetryModule = module {
    single { telemetryConfigFromBuildConfig() }
    singleOf(::TelemetryManager)
    // single: the "once per launch" guard lives in it
    singleOf(::appStartedTelemetry)
    viewModelOf(::TelemetryViewModel)
    singleOf(::DeviceConnectionTelemetry) {
        bind<MessagesReceiverRepository>()
    }
}
