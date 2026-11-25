package io.github.openflocon.domain.webview.usecase

import io.github.openflocon.domain.webview.models.WebViewEventDomainModel
import io.github.openflocon.domain.webview.repository.WebViewRepository
import kotlinx.coroutines.flow.Flow

class ObserveWebViewEventUseCase(
    private val repository: WebViewRepository,
) {
    operator fun invoke(id: String): Flow<WebViewEventDomainModel?> = repository.observeEvent(id)
}
