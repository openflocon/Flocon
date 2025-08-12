package io.github.openflocon.data.local.deeplink

import io.github.openflocon.data.core.deeplink.datasource.DeeplinkLocalDataSource
import io.github.openflocon.data.local.deeplink.datasource.LocalDeeplinkDataSourceRoom
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val deeplinkModule = module {
    singleOf(::LocalDeeplinkDataSourceRoom) bind DeeplinkLocalDataSource::class
}
