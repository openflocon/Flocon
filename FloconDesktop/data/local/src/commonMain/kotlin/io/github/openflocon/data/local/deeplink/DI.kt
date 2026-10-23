package io.github.openflocon.data.local.deeplink

import io.github.openflocon.data.core.deeplink.datasource.DeeplinkLocalDataSource
import io.github.openflocon.data.local.JSON
import io.github.openflocon.data.local.deeplink.datasource.LocalDeeplinkDataSourceRoom
import org.koin.dsl.bind
import org.koin.dsl.module

internal val deeplinkModule = module {
    single {
        LocalDeeplinkDataSourceRoom(deeplinkDao = get(), deeplinkVariableDao = get(), json = get(JSON))
    } bind DeeplinkLocalDataSource::class
}
