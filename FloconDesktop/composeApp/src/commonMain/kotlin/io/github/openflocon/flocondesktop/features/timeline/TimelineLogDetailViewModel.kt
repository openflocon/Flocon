package io.github.openflocon.flocondesktop.features.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.copied_to_clipboard
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.timelinelog.usecase.ObserveTimelineLogUseCase
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.features.timeline.mapper.toDetailUi
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineLogDetailUiModel
import io.github.openflocon.library.designsystem.common.copyToClipboard
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

internal class TimelineLogDetailViewModel(
    id: Long,
    observeTimelineLogUseCase: ObserveTimelineLogUseCase,
    private val feedbackDisplayer: FeedbackDisplayer,
) : ViewModel() {

    // null while the log is not loaded, or when it does not exist anymore
    val uiState = observeTimelineLogUseCase(id)
        .map { it?.toDetailUi() }
        .stateInWhileSubscribed<TimelineLogDetailUiModel?>(default = null)

    fun onCopyValue() {
        copy(uiState.value?.value ?: return)
    }

    fun onCopyParams() {
        val params = uiState.value?.params ?: return
        copy(params.joinToString(separator = "\n") { (key, value) -> "$key: $value" })
    }

    private fun copy(text: String) {
        viewModelScope.launch {
            copyToClipboard(text)
            feedbackDisplayer.displayMessage(getString(Res.string.copied_to_clipboard))
        }
    }
}
