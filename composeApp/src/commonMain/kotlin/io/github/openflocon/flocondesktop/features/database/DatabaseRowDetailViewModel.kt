package io.github.openflocon.flocondesktop.features.database

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.copied_to_clipboard
import io.github.openflocon.domain.database.models.DatabaseRowUpdateResultDomainModel
import io.github.openflocon.domain.database.usecase.UpdateDatabaseRowUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.features.database.delegate.DatabaseRowUpdatedNotifier
import io.github.openflocon.flocondesktop.features.database.mapper.displayRowUpdateResult
import io.github.openflocon.flocondesktop.features.database.mapper.toDomain
import io.github.openflocon.flocondesktop.features.database.mapper.toRowDetailUiModel
import io.github.openflocon.flocondesktop.features.database.mapper.toRowMarkdown
import io.github.openflocon.flocondesktop.features.database.model.DatabaseRowDetailAction
import io.github.openflocon.flocondesktop.features.database.model.DatabaseRowDetailUiModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseRowEditTargetUiModel
import io.github.openflocon.library.designsystem.common.copyToClipboard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class DatabaseRowDetailViewModel(
    private val params: Params,
    private val updateDatabaseRowUseCase: UpdateDatabaseRowUseCase,
    private val rowUpdatedNotifier: DatabaseRowUpdatedNotifier,
    private val feedbackDisplayer: FeedbackDisplayer,
    /** The update goes on, and reports its result, when the panel is closed. */
    private val applicationScope: CoroutineScope,
) : ViewModel() {

    @Immutable
    data class Params(
        val columns: List<String>,
        val values: List<String?>,
        val editTarget: DatabaseRowEditTargetUiModel?,
    )

    private var originalValues = params.values
    private var editedValues = params.values
    private var isSaving = false

    /** A Compose state so that typing in the fields stays responsive. */
    var uiState: DatabaseRowDetailUiModel by mutableStateOf(buildUiState())
        private set

    fun onAction(action: DatabaseRowDetailAction) {
        when (action) {
            is DatabaseRowDetailAction.ValueChanged -> edit(action.index, action.value)
            is DatabaseRowDetailAction.NullChanged -> edit(action.index, if (action.isNull) null else "")
            is DatabaseRowDetailAction.CopyValue -> copy(action.value)
            DatabaseRowDetailAction.CopyAll -> copy(toRowMarkdown(params.columns, editedValues))
            DatabaseRowDetailAction.Update -> update()
        }
    }

    private fun edit(index: Int, value: String?) {
        if (index !in editedValues.indices) return
        editedValues = editedValues.toMutableList().apply { set(index, value) }
        uiState = buildUiState()
    }

    private fun update() {
        val target = params.editTarget ?: return
        if (!uiState.canUpdate) return
        val original = originalValues
        val edited = editedValues
        isSaving = true
        uiState = buildUiState()

        val saving = applicationScope.async {
            updateDatabaseRowUseCase(
                target = target.toDomain(),
                original = original,
                edited = edited,
            ).also {
                if (it == DatabaseRowUpdateResultDomainModel.Updated) {
                    rowUpdatedNotifier.notifyUpdated(target)
                }
                feedbackDisplayer.displayRowUpdateResult(it)
            }
        }
        viewModelScope.launch {
            if (saving.await() == DatabaseRowUpdateResultDomainModel.Updated) {
                originalValues = edited
            }
            isSaving = false
            uiState = buildUiState()
        }
    }

    private fun copy(text: String) {
        viewModelScope.launch {
            copyToClipboard(text)
            feedbackDisplayer.displayMessage(getString(Res.string.copied_to_clipboard))
        }
    }

    private fun buildUiState() = toRowDetailUiModel(
        columns = params.columns,
        original = originalValues,
        edited = editedValues,
        editTarget = params.editTarget,
        isSaving = isSaving,
    )
}
