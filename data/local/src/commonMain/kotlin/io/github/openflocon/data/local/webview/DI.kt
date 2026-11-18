package io.github.openflocon.data.local.webview

import io.github.openflocon.data.core.webview.datasource.WebViewLocalDataSource
import io.github.openflocon.data.local.webview.datasource.WebViewLocalDataSourceRoom
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val webViewLocalModule = module {
    singleOf(::WebViewLocalDataSourceRoom) bind WebViewLocalDataSource::class
}
