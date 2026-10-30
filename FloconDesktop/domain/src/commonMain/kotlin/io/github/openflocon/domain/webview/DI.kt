package io.github.openflocon.domain.webview

import io.github.openflocon.domain.webview.usecase.ClearAllWebViewEventsUseCase
import io.github.openflocon.domain.webview.usecase.ObserveWebViewEventsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val webViewDomainModule = module {
    factoryOf(::ObserveWebViewEventsUseCase)
    factoryOf(::ClearAllWebViewEventsUseCase)
}
