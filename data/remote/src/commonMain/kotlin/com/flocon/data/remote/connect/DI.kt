package com.flocon.data.remote.connect

import io.github.openflocon.domain.connect.repository.LocalNetworkRepository
import org.koin.dsl.module

internal val connectModule = module {
    single<LocalNetworkRepository> { getLocalNetworkRepository() }
}
