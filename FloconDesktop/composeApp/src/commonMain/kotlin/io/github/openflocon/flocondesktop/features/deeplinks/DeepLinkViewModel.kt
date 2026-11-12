package io.github.openflocon.flocondesktop.features.deeplinks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.copied_to_clipboard
import flocondesktop.composeapp.generated.resources.deeplink_removed
import flocondesktop.composeapp.generated.resources.deeplink_send_error
import flocondesktop.composeapp.generated.resources.fill_deeplink_parts
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.deeplink.models.DeeplinkVariableDomainModel
import io.github.openflocon.domain.deeplink.usecase.ExecuteDeeplinkUseCase
import io.github.openflocon.domain.deeplink.usecase.ObserveCurrentDeviceDeeplinkHistoryUseCase
import io.github.openflocon.domain.deeplink.usecase.ObserveCurrentDeviceDeeplinkUseCase
import io.github.openflocon.domain.deeplink.usecase.RemoveFromDeeplinkHistoryUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceCapabilitiesUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.features.deeplinks.mapper.buildLink
import io.github.openflocon.flocondesktop.features.deeplinks.mapper.mapToUi
import io.github.openflocon.flocondesktop.features.deeplinks.mapper.parseFreeformLink
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkPart
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkScreenAction
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkScreenState
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkTabUiModel
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkVariableViewState
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkViewState
import io.github.openflocon.flocondesktop.telemetry.TelemetryFeature
import io.github.openflocon.flocondesktop.telemetry.TelemetryManager
import io.github.openflocon.library.designsystem.common.copyToClipboard
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
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
    observeCurrentDeviceCapabilitiesUseCase: ObserveCurrentDeviceCapabilitiesUseCase,
    private val executeDeeplinkUseCase: ExecuteDeeplinkUseCase,
    private val removeFromDeeplinkHistoryUseCase: RemoveFromDeeplinkHistoryUseCase,
    private val telemetryManager: TelemetryManager,
) : ViewModel() {

    private val variableValues = MutableStateFlow<PersistentMap<String, String>>(persistentMapOf())
    private val openTabs = MutableStateFlow<List<DeeplinkTabUiModel>>(listOf(DeeplinkTabUiModel.Freeform))
    private val selectedTabId = MutableStateFlow(DeeplinkTabUiModel.Freeform.id)

    // a Compose state: the text field stays responsive while typing
    var freeformLink by mutableStateOf("")
        private set

    private val tabsState = combine(openTabs, selectedTabId) { tabs, selectedId -> tabs to selectedId }

    val state: StateFlow<DeeplinkScreenState> = combine(
        observeCurrentDeviceDeeplinkUseCase(),
        observeCurrentDeviceDeeplinkHistoryUseCase(),
        variableValues.asStateFlow(),
        observeCurrentDeviceCapabilitiesUseCase(),
        tabsState,
    ) { deepLinks, history, variablesValues, capabilities, (tabs, selectedId) ->
        val links = mapToUi(
            history = history,
            deepLinks = deepLinks.deeplinks,
            variables = deepLinks.variables,
            variableValues = variablesValues
        )
        // a link removed from the history closes its tab
        val visibleTabs = tabs
            .filter { tab ->
                tab !is DeeplinkTabUiModel.Link ||
                    links.any { it.deeplinkId == tab.deeplinkId && it.isHistory == tab.isHistory }
            }
            .ifEmpty { listOf(DeeplinkTabUiModel.Freeform) }

        DeeplinkScreenState(
            deepLinks = links,
            variables = deepLinks.variables.map { variable ->
                DeeplinkVariableViewState(
                    name = variable.name,
                    description = variable.description,
                    value = when (val mode = variable.mode) {
                        is DeeplinkVariableDomainModel.Mode.AutoComplete -> variablesValues.getOrDefault(
                            key = variable.name,
                            defaultValue = mode.suggestions.firstOrNull() ?: ""
                        )

                        is DeeplinkVariableDomainModel.Mode.Input -> variablesValues.getOrDefault(
                            key = variable.name,
                            defaultValue = ""
                        )
                    },
                    mode = when (val mode = variable.mode) {
                        DeeplinkVariableDomainModel.Mode.Input ->
                            DeeplinkVariableViewState.Mode.Input

                        is DeeplinkVariableDomainModel.Mode.AutoComplete ->
                            DeeplinkVariableViewState.Mode.AutoComplete(mode.suggestions)
                    },
                )
            },
            tabs = visibleTabs,
            selectedTab = visibleTabs.firstOrNull { it.id == selectedId } ?: visibleTabs.first(),
        ).let {
            it.copy(
                showEmptyState = it.deepLinks.isEmpty() && it.variables.isEmpty(),
                canSend = capabilities?.deeplinks ?: true,
            )
        }
    }
        .stateInWhileSubscribed(DeeplinkScreenState(emptyList(), emptyList()))

    fun onAction(action: DeeplinkScreenAction) {
        when (action) {
            is DeeplinkScreenAction.Submit -> submit(action.item, action.values)
            DeeplinkScreenAction.SubmitFreeform -> submitFreeform()
            is DeeplinkScreenAction.CopyLink -> copyLink(action.link)
            is DeeplinkScreenAction.SetFreeformLink -> freeformLink = action.value
            is DeeplinkScreenAction.RemoveFromHistory -> removeFromHistory(action.item)
            is DeeplinkScreenAction.SetVariable -> setVariable(action.name, action.value)
            is DeeplinkScreenAction.OpenTab -> openTab(action.tab)
            is DeeplinkScreenAction.SelectTab -> selectedTabId.value = action.id
            is DeeplinkScreenAction.CloseTab -> closeTab(action.id)
        }
    }

    private fun openTab(tab: DeeplinkTabUiModel) {
        openTabs.update { tabs -> if (tabs.any { it.id == tab.id }) tabs else tabs + tab }
        selectedTabId.value = tab.id
    }

    private fun closeTab(id: String) {
        val tabs = openTabs.value
        val index = tabs.indexOfFirst { it.id == id }
        if (index < 0) return
        val remaining = tabs.filterIndexed { i, _ -> i != index }
        openTabs.value = remaining
        if (selectedTabId.value == id) {
            // the neighbour tab takes the focus
            selectedTabId.value = remaining.getOrNull(index.coerceAtMost(remaining.lastIndex))?.id
                ?: DeeplinkTabUiModel.Freeform.id
        }
    }

    private fun setVariable(name: String, value: String) {
        variableValues.update { current ->
            if (value.isNotEmpty()) {
                current.putting(name, value)
            } else {
                current.removing(name)
            }
        }
    }

    private fun removeFromHistory(viewState: DeeplinkViewState) {
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

    private fun submitFreeform() {
        val parts = parseFreeformLink(freeformLink.trim(), state.value.variables)
        // a variable without value would be sent as "{name}"
        if (parts.isEmpty() || parts.any { it is DeeplinkPart.Variable && !it.isResolved }) return
        execute(deeplink = parts.buildLink(), deeplinkId = FREEFORM_DEEPLINK_ID, saveIntoHistory = true)
    }

    private fun copyLink(link: String) {
        viewModelScope.launch {
            copyToClipboard(link)
            feedbackDisplayer.displayMessage(getString(Res.string.copied_to_clipboard))
        }
    }

    private fun submit(viewState: DeeplinkViewState, values: Map<DeeplinkPart.TextField, String>) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val numberOfTextFields = viewState.parts.count { it is DeeplinkPart.TextField }
            if (numberOfTextFields != values.values.filterNot { it.isBlank() }.size) {
                feedbackDisplayer.displayMessage(
                    getString(Res.string.fill_deeplink_parts),
                    type = FeedbackDisplayer.MessageType.Error,
                )
                return@launch
            }

            val deeplink = viewState.parts.joinToString(separator = "") {
                when (it) {
                    is DeeplinkPart.Text -> it.value

                    is DeeplinkPart.TextField -> values[it] ?: ""

                    // already resolved with the current variable values
                    is DeeplinkPart.Variable -> it.value
                }
            }

            telemetryManager.featureUsed(TelemetryFeature.DeeplinkLaunched)
            execute(
                deeplink = deeplink,
                deeplinkId = viewState.deeplinkId,
                saveIntoHistory = numberOfTextFields != 0,
            )
        }
    }

    private fun execute(deeplink: String, deeplinkId: Long, saveIntoHistory: Boolean) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            executeDeeplinkUseCase(
                deeplink = deeplink,
                deeplinkId = deeplinkId,
                saveIntoHistory = saveIntoHistory,
            )
                .alsoFailure {
                    it.printStackTrace()
                    feedbackDisplayer.displayMessage(getString(Res.string.deeplink_send_error))
                }
        }
    }

    private companion object {
        const val FREEFORM_DEEPLINK_ID = -1L
    }
}
