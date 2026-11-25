package io.github.openflocon.flocondesktop.features.webview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.copied_to_clipboard
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.webview.usecase.ObserveWebViewEventUseCase
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.features.webview.mapper.toDetailUi
import io.github.openflocon.flocondesktop.features.webview.model.WebViewEventDetailUiModel
import io.github.openflocon.library.designsystem.common.copyToClipboard
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

internal class WebViewEventDetailViewModel(
    id: String,
    observeWebViewEventUseCase: ObserveWebViewEventUseCase,
    private val feedbackDisplayer: FeedbackDisplayer,
) : ViewModel() {

    // null while the event is not loaded, or when it does not exist anymore
    val uiState = observeWebViewEventUseCase(id)
        .map { it?.toDetailUi() }
        .stateInWhileSubscribed<WebViewEventDetailUiModel?>(default = null)

    fun onCopyMessage() {
        copy(uiState.value?.message ?: return)
    }

    fun onCopyResult() {
        copy(uiState.value?.result ?: return)
    }

    private fun copy(text: String) {
        viewModelScope.launch {
            copyToClipboard(text)
            feedbackDisplayer.displayMessage(getString(Res.string.copied_to_clipboard))
        }
    }
}
