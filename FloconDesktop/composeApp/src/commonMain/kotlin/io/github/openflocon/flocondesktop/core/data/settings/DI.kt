package io.github.openflocon.flocondesktop.core.data.settings

import io.github.openflocon.flocondesktop.core.data.settings.usecase.ObserveNetworkSettingsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val settingsModule = module {
    factoryOf(::ObserveNetworkSettingsUseCase)
}
