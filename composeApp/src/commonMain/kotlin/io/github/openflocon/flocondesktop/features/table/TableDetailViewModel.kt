package io.github.openflocon.flocondesktop.features.table

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.copied_to_clipboard
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.table.usecase.ObserveCurrentDeviceTableItemUseCase
import io.github.openflocon.flocondesktop.common.ui.detail.toCopyableText
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.features.table.mapper.toDetailUi
import io.github.openflocon.flocondesktop.features.table.model.TableDetailUiModel
import io.github.openflocon.library.designsystem.common.copyToClipboard
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class TableDetailViewModel(
    id: String,
    observeCurrentDeviceTableItemUseCase: ObserveCurrentDeviceTableItemUseCase,
    private val feedbackDisplayer: FeedbackDisplayer,
) : ViewModel() {

    val uiState = observeCurrentDeviceTableItemUseCase(id)
        .map { it?.toDetailUi(itemId = id) }
        .stateInWhileSubscribed<TableDetailUiModel?>(default = null)

    fun onCopyColumns() {
        val columns = uiState.value?.columns?.takeIf { it.isNotEmpty() } ?: return
        viewModelScope.launch {
            copyToClipboard(columns.map { it.name to it.value }.toCopyableText())
            feedbackDisplayer.displayMessage(getString(Res.string.copied_to_clipboard))
        }
    }
}
