package com.flocon.data.remote.webview

import com.flocon.data.remote.webview.datasource.WebViewRemoteDataSourceImpl
import io.github.openflocon.data.core.webview.datasource.WebViewRemoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val webViewModule = module {
    singleOf(::WebViewRemoteDataSourceImpl) bind WebViewRemoteDataSource::class
}
