package io.github.openflocon.flocondesktop.features.proxy

import io.github.openflocon.domain.proxy.repository.ProxyRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.io.File

actual val proxyPlatformModule: Module = module {
    // the CA is kept next to the other Flocon files, so the phone installs it only once
    single { ProxyManager(dataDir = File(System.getProperty("user.home"), ".flocon"), scope = get()) }
    singleOf(::ProxyRepositoryImpl) {
        bind<ProxyRepository>()
    }
}
