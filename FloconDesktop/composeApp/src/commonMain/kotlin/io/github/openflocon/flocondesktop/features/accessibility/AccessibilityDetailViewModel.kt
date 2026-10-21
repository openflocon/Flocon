package io.github.openflocon.flocondesktop.features.accessibility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.copied_to_clipboard
import io.github.openflocon.domain.accessibility.usecase.ObserveCurrentDeviceAccessibilityEventUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.common.ui.detail.toCopyableText
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.features.accessibility.mapper.mapToDetailUi
import io.github.openflocon.flocondesktop.features.accessibility.model.AccessibilityDetailUiModel
import io.github.openflocon.library.designsystem.common.copyToClipboard
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class AccessibilityDetailViewModel(
    id: String,
    observeCurrentDeviceAccessibilityEventUseCase: ObserveCurrentDeviceAccessibilityEventUseCase,
    private val feedbackDisplayer: FeedbackDisplayer,
) : ViewModel() {

    val uiState = observeCurrentDeviceAccessibilityEventUseCase(id)
        .map { it?.mapToDetailUi() }
        .stateInWhileSubscribed<AccessibilityDetailUiModel?>(default = null)

    fun onCopyFields() {
        val detail = uiState.value ?: return
        copy(detail.fields().toCopyableText())
    }

    fun onCopyExtras() {
        val extras = uiState.value?.extras?.takeIf { it.isNotEmpty() } ?: return
        copy(extras.toCopyableText())
    }

    private fun copy(text: String) {
        viewModelScope.launch {
            copyToClipboard(text)
            feedbackDisplayer.displayMessage(getString(Res.string.copied_to_clipboard))
        }
    }
}

// the fields of the event that are set, to copy
private fun AccessibilityDetailUiModel.fields() = listOfNotNull(
    "time" to timeFormatted,
    "type" to type,
    text?.let { "text" to it },
    contentDescription?.let { "contentDescription" to it },
    className?.let { "className" to it },
    screen?.let { "screen" to it },
    bounds?.let { "bounds" to it },
)
