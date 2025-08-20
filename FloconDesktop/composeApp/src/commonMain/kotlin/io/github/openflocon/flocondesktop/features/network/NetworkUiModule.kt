package io.github.openflocon.flocondesktop.features.network

import io.github.openflocon.flocondesktop.features.network.badquality.BadQualityNetworkViewModel
import io.github.openflocon.flocondesktop.features.network.delegate.HeaderDelegate
import io.github.openflocon.flocondesktop.features.network.mock.NetworkMocksViewModel
import io.github.openflocon.flocondesktop.messages.ui.MessagesServerDelegate
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val networkModule = module {
    viewModelOf(::NetworkViewModel)
    factoryOf(::MessagesServerDelegate)
    factoryOf(::HeaderDelegate)
    factoryOf(::SortAndFilterNetworkItemsProcessor)

    viewModelOf(::NetworkMocksViewModel)

    viewModelOf(::BadQualityNetworkViewModel)
}
