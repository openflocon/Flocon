package io.github.openflocon.flocondesktop.features.deeplinks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.deeplink_removed
import flocondesktop.composeapp.generated.resources.fill_deeplink_parts
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.deeplink.models.DeeplinkVariableDomainModel
import io.github.openflocon.domain.deeplink.usecase.ExecuteDeeplinkUseCase
import io.github.openflocon.domain.deeplink.usecase.ObserveCurrentDeviceDeeplinkHistoryUseCase
import io.github.openflocon.domain.deeplink.usecase.ObserveCurrentDeviceDeeplinkUseCase
import io.github.openflocon.domain.deeplink.usecase.RemoveFromDeeplinkHistoryUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.features.deeplinks.mapper.mapToUi
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkPart
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkScreenState
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkVariableViewState
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class DeepLinkViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
    observeCurrentDeviceDeeplinkUseCase: ObserveCurrentDeviceDeeplinkUseCase,
    observeCurrentDeviceDeeplinkHistoryUseCase: ObserveCurrentDeviceDeeplinkHistoryUseCase,
    private val executeDeeplinkUseCase: ExecuteDeeplinkUseCase,
    private val removeFromDeeplinkHistoryUseCase: RemoveFromDeeplinkHistoryUseCase,
) : ViewModel() {

    private val variableValues = MutableStateFlow<Map<String, String>>(emptyMap())

    val state: StateFlow<DeeplinkScreenState> = combine(
        observeCurrentDeviceDeeplinkUseCase(),
        observeCurrentDeviceDeeplinkHistoryUseCase(),
        variableValues.asStateFlow()
    ) { deepLinks, history, variablesValues ->
        DeeplinkScreenState(
            deepLinks = mapToUi(
                history = history,
                deepLinks = deepLinks.deeplinks,
                variableValues = variablesValues
            ),
            variables = deepLinks.variables.map { variable ->
                DeeplinkVariableViewState(
                    name = variable.name,
                    description = variable.description,
                    value = variablesValues.getOrDefault(
                        variable.name,
                        ""
                    ),
                    mode = when (val m = variable.mode) {
                        DeeplinkVariableDomainModel.Mode.Input ->
                            DeeplinkVariableViewState.Mode.Input

                        is DeeplinkVariableDomainModel.Mode.AutoComplete ->
                            DeeplinkVariableViewState.Mode.AutoComplete(m.suggestions)
                    }
                )
            }
        )
    }
        .stateInWhileSubscribed(DeeplinkScreenState(emptyList(), emptyList()))

    fun setVariable(name: String, value: String) {
        variableValues.update { current -> current + (name to value) }
    }

    fun removeFromHistory(viewState: DeeplinkViewState) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            removeFromDeeplinkHistoryUseCase(
                deeplinkId = viewState.deeplinkId,
            )
            feedbackDisplayer.displayMessage(
                getString(Res.string.deeplink_removed),
                type = FeedbackDisplayer.MessageType.Error,
            )
        }
    }

    fun submit(viewState: DeeplinkViewState, values: Map<DeeplinkPart.TextField, String>) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val numberOfTextFields = viewState.parts.count { it is DeeplinkPart.TextField }
            if (numberOfTextFields != values.values.filterNot { it.isBlank() }.size) {
                feedbackDisplayer.displayMessage(
                    getString(Res.string.fill_deeplink_parts),
                    type = FeedbackDisplayer.MessageType.Error,
                )
                return@launch
            }

            val currentVariableValues = variableValues.value
            val deeplink = viewState.parts.joinToString(separator = "") {
                when (it) {
                    is DeeplinkPart.Text -> it.value
                    is DeeplinkPart.TextField -> values[it] ?: ""
                    is DeeplinkPart.Variable -> currentVariableValues[it.value] ?: it.value
                }
            }

            executeDeeplinkUseCase(
                deeplink = deeplink,
                deeplinkId = viewState.deeplinkId,
                saveIntoHistory = viewState.deeplinkId == -1L || numberOfTextFields != 0
            )
                .alsoFailure {
                    it.printStackTrace()
                    feedbackDisplayer.displayMessage(message = "Error while sending deeplink")
                }
        }
    }
}
