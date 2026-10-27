package io.github.openflocon.flocondesktop.features.deeplinks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.copied_to_clipboard
import io.github.openflocon.domain.deeplink.usecase.ObserveCurrentDeviceDeeplinkHistoryUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkPanelUiModel
import io.github.openflocon.library.designsystem.common.copyToClipboard
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class DeeplinkPanelViewModel(
    id: Long,
    observeCurrentDeviceDeeplinkHistoryUseCase: ObserveCurrentDeviceDeeplinkHistoryUseCase,
    private val feedbackDisplayer: FeedbackDisplayer,
) : ViewModel() {

    val uiState = observeCurrentDeviceDeeplinkHistoryUseCase()
        .map { history ->
            history.firstOrNull { it.id == id }?.let {
                DeeplinkPanelUiModel(
                    link = it.link,
                    label = it.label,
                    description = it.description,
                )
            }
        }
        .stateInWhileSubscribed<DeeplinkPanelUiModel?>(default = null)

    fun onCopyLink() {
        val link = uiState.value?.link ?: return
        viewModelScope.launch {
            copyToClipboard(link)
            feedbackDisplayer.displayMessage(getString(Res.string.copied_to_clipboard))
        }
    }
}
