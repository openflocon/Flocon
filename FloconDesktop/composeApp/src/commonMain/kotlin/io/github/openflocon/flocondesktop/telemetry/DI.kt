package io.github.openflocon.flocondesktop.telemetry

import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val telemetryModule = module {
    single { TelemetryConfig.fromBuildConfig() }
    single<TelemetryPreferences> { TelemetryPreferencesImpl() }
    single<TelemetryClient> {
        val config = get<TelemetryConfig>()
        if (config.isAvailable) PostHogTelemetryClient(config) else NoOpTelemetryClient
    }
    single {
        TelemetryManager(
            preferences = get(),
            client = get(),
            applicationScope = get(),
            config = get(),
        )
    }
    viewModelOf(::TelemetryViewModel)
    single {
        DeviceConnectionTelemetry(remoteDeviceDataSource = get(), telemetryManager = get())
    } bind MessagesReceiverRepository::class
}
