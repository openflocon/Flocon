package io.github.openflocon.flocondesktop.features.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.copied_to_clipboard
import io.github.openflocon.domain.database.models.toFullSql
import io.github.openflocon.domain.database.usecase.ObserveDatabaseQueryLogUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.common.ui.detail.toCopyableText
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.features.database.model.DatabaseQueryLogPanelUiModel
import io.github.openflocon.library.designsystem.common.copyToClipboard
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseQueryLogPanelViewModel(
    id: Int,
    observeDatabaseQueryLogUseCase: ObserveDatabaseQueryLogUseCase,
    private val feedbackDisplayer: FeedbackDisplayer,
) : ViewModel() {

    val uiState = observeDatabaseQueryLogUseCase(id)
        .filterNotNull()
        .map {
            DatabaseQueryLogPanelUiModel(
                dbName = it.dbId,
                sql = it.toFullSql(),
                rawSql = it.sqlQuery,
                arguments = it.bindArgs.orEmpty(),
                dateFormatted = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date(it.timestamp)),
                isTransaction = it.isTransaction,
            )
        }
        .stateInWhileSubscribed<DatabaseQueryLogPanelUiModel?>(default = null)

    fun onCopySql() {
        uiState.value?.sql?.let { copy(it) }
    }

    fun onCopyArguments() {
        val arguments = uiState.value?.arguments?.takeIf { it.isNotEmpty() } ?: return
        copy(arguments.mapIndexed { index, value -> "${index + 1}" to value }.toCopyableText())
    }

    private fun copy(text: String) {
        viewModelScope.launch {
            copyToClipboard(text)
            feedbackDisplayer.displayMessage(getString(Res.string.copied_to_clipboard))
        }
    }
}
