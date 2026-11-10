package io.github.openflocon.flocondesktop.features.webview

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val webViewModule = module {
    viewModelOf(::WebViewViewModel)
}
