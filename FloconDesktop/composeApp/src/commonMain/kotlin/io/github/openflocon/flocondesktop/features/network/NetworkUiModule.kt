package io.github.openflocon.flocondesktop.features.network

import io.github.openflocon.flocondesktop.features.network.badquality.BadQualityNetworkViewModel
import io.github.openflocon.flocondesktop.features.network.list.NetworkViewModel
import io.github.openflocon.flocondesktop.features.network.list.delegate.HeaderDelegate
import io.github.openflocon.flocondesktop.features.network.list.delegate.OpenBodyDelegate
import io.github.openflocon.flocondesktop.features.network.mock.NetworkMocksViewModel
import io.github.openflocon.flocondesktop.features.network.mock.processor.ExportMocksProcessor
import io.github.openflocon.flocondesktop.features.network.mock.processor.ImportMocksProcessor
import io.github.openflocon.flocondesktop.features.network.websocket.NetworkWebsocketMockViewModel
import io.github.openflocon.flocondesktop.messages.ui.MessagesServerDelegate
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val networkModule = module {
    viewModelOf(::NetworkViewModel)
    factoryOf(::MessagesServerDelegate)
    factoryOf(::HeaderDelegate)
    factoryOf(::OpenBodyDelegate)

    viewModelOf(::NetworkMocksViewModel)
    factoryOf(::ExportMocksProcessor)
    factoryOf(::ImportMocksProcessor)

    viewModelOf(::BadQualityNetworkViewModel)
    viewModelOf(::NetworkWebsocketMockViewModel)
}
