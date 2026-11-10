package io.github.openflocon.domain.webview.usecase

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.webview.repository.WebViewRepository

class ClearAllWebViewEventsUseCase(
    private val repository: WebViewRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke() {
        getCurrentDeviceIdAndPackageNameUseCase()?.let {
            repository.clearAll(it)
        }
    }
}
