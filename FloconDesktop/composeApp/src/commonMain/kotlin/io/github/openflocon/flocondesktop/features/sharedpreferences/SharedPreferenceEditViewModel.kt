package io.github.openflocon.flocondesktop.features.sharedpreferences

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.copied_to_clipboard
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.sharedpreference.models.SharedPreferenceRowDomainModel
import io.github.openflocon.domain.sharedpreference.usecase.EditSharedPrefFieldUseCase
import io.github.openflocon.domain.sharedpreference.usecase.ObserveCurrentDeviceSharedPreferenceValuesUseCase
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.PreferenceEditStatus
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.PreferenceEditTab
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.PreferenceEditUiModel
import io.github.openflocon.library.designsystem.common.copyToClipboard
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

/**
 * Editor of the value of one preference (a json or a long text), displayed in a right panel.
 * The text is a Compose state so typing stays responsive.
 */
class SharedPreferenceEditViewModel(
    private val key: String,
    observeValuesUseCase: ObserveCurrentDeviceSharedPreferenceValuesUseCase,
    private val editSharedPrefFieldUseCase: EditSharedPrefFieldUseCase,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    // the value known from the app
    private var observed by mutableStateOf("")

    // the value just sent: the baseline until the app reports a new value
    private var sent by mutableStateOf<String?>(null)

    // null: the user did not edit, the text is the baseline
    private var edited by mutableStateOf<String?>(null)
    private var tab by mutableStateOf(PreferenceEditTab.Formatted)

    val uiState: State<PreferenceEditUiModel> = derivedStateOf { buildUiModel() }

    init {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            observeValuesUseCase()
                .map { rows ->
                    (rows.firstOrNull { it.key == key }?.value as? SharedPreferenceRowDomainModel.Value.StringValue)?.value
                }
                .distinctUntilChanged()
                .collect { value ->
                    if (value != null) {
                        observed = value
                        sent = null
                    }
                }
        }
    }

    private fun buildUiModel(): PreferenceEditUiModel {
        val baseline = sent ?: observed
        val isJson = isValidJson(baseline)
        val pretty = tab == PreferenceEditTab.Formatted
        val text = edited ?: if (isJson) formatJson(baseline, pretty) else baseline
        val modified = if (isJson) {
            // formatting only is not a modification
            formatJson(text, pretty = false) != formatJson(baseline, pretty = false)
        } else {
            text != baseline
        }
        val valid = !isJson || isValidJson(text)
        val status = when {
            !modified -> PreferenceEditStatus.Unchanged
            !isJson -> PreferenceEditStatus.Modified
            valid -> PreferenceEditStatus.ModifiedValidJson
            else -> PreferenceEditStatus.InvalidJson
        }
        val sizeLabel = SharedPreferenceRowDomainModel(key, SharedPreferenceRowDomainModel.Value.StringValue(baseline))
            .toUi().collapsible?.sizeLabel
            ?: byteSizeLabel(baseline.encodeToByteArray().size)
        return PreferenceEditUiModel(
            key = key,
            isJson = isJson,
            sizeLabel = sizeLabel,
            tab = tab,
            text = text,
            lineCount = text.count { it == '\n' } + 1,
            status = status,
            canSend = modified && valid,
        )
    }

    fun onTextChanged(text: String) {
        edited = text
    }

    fun onTabSelected(newTab: PreferenceEditTab) {
        val current = edited
        if (current != null && isValidJson(current)) {
            edited = formatJson(current, pretty = newTab == PreferenceEditTab.Formatted)
        }
        tab = newTab
    }

    fun onCancel() {
        edited = null
    }

    fun onCopy() {
        val text = uiState.value.text
        viewModelScope.launch {
            copyToClipboard(text)
            feedbackDisplayer.displayMessage(getString(Res.string.copied_to_clipboard))
        }
    }

    fun onSend() {
        val state = uiState.value
        if (!state.canSend) return
        // a json is sent compact, whatever the tab
        val value = if (state.isJson) formatJson(state.text, pretty = false) else state.text
        sent = value
        edited = null
        viewModelScope.launch(dispatcherProvider.viewModel) {
            editSharedPrefFieldUseCase(key, SharedPreferenceRowDomainModel.Value.StringValue(value))
        }
    }
}
