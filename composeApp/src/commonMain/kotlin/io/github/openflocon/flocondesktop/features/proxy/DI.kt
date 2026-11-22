package io.github.openflocon.flocondesktop.features.proxy

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** The proxy process runs on the computer: its repository is in the platform source set. */
expect val proxyPlatformModule: Module

val proxyModule = module {
    includes(proxyPlatformModule)
    viewModelOf(::ProxyViewModel)
}
