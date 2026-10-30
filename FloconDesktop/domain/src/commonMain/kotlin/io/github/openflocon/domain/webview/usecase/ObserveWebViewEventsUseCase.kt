package io.github.openflocon.domain.webview.usecase

import androidx.paging.PagingData
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.webview.models.WebViewEventDomainModel
import io.github.openflocon.domain.webview.models.WebViewEventKind
import io.github.openflocon.domain.webview.repository.WebViewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveWebViewEventsUseCase(
    private val repository: WebViewRepository,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(
        kind: WebViewEventKind?,
        filter: String?,
    ): Flow<PagingData<WebViewEventDomainModel>> = observeCurrentDeviceIdAndPackageNameUseCase()
        .flatMapLatest { current ->
            if (current == null) {
                flowOf(PagingData.empty())
            } else {
                repository.observeEvents(
                    deviceIdAndPackageName = current,
                    kind = kind,
                    filter = filter?.takeIf { it.isNotBlank() },
                )
            }
        }
}
