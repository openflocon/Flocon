package io.github.openflocon.flocondesktop.features.adbcommander

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.copied_to_clipboard
import io.github.openflocon.domain.adbcommander.usecase.ObserveCommandHistoryItemUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.features.adbcommander.mapper.toPanelUiModel
import io.github.openflocon.flocondesktop.features.adbcommander.model.AdbCommandPanelUiModel
import io.github.openflocon.library.designsystem.common.copyToClipboard
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class AdbCommandPanelViewModel(
    id: Long,
    observeCommandHistoryItemUseCase: ObserveCommandHistoryItemUseCase,
    private val feedbackDisplayer: FeedbackDisplayer,
) : ViewModel() {

    val uiState = observeCommandHistoryItemUseCase(id)
        .filterNotNull()
        .map { it.toPanelUiModel() }
        .stateInWhileSubscribed<AdbCommandPanelUiModel?>(default = null)

    fun onCopyCommand() {
        uiState.value?.command?.let { copy(it) }
    }

    fun onCopyOutput() {
        uiState.value?.output?.takeIf { it.isNotEmpty() }?.let { copy(it) }
    }

    private fun copy(text: String) {
        viewModelScope.launch {
            copyToClipboard(text)
            feedbackDisplayer.displayMessage(getString(Res.string.copied_to_clipboard))
        }
    }
}
