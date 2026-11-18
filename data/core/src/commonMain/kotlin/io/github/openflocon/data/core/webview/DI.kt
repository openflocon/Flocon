package io.github.openflocon.data.core.webview

import io.github.openflocon.data.core.webview.repository.WebViewRepositoryImpl
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import io.github.openflocon.domain.webview.repository.WebViewRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val webViewModule = module {
    singleOf(::WebViewRepositoryImpl) {
        bind<WebViewRepository>()
        bind<MessagesReceiverRepository>()
    }
}
