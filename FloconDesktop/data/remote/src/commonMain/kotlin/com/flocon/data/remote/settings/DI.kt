package com.flocon.data.remote.settings

import io.github.openflocon.domain.settings.repository.NetworkAddressRepository
import org.koin.dsl.module

internal val settingsModule = module {
    single<NetworkAddressRepository> { getNetworkAddressRepository() }
}
