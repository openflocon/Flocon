package io.github.openflocon.flocondesktop.features.deeplinks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.deeplink_removed
import flocondesktop.composeapp.generated.resources.fill_deeplink_parts
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.combines
import io.github.openflocon.domain.deeplink.usecase.ExecuteDeeplinkUseCase
import io.github.openflocon.domain.deeplink.usecase.ObserveCurrentDeviceDeeplinkHistoryUseCase
import io.github.openflocon.domain.deeplink.usecase.ObserveCurrentDeviceDeeplinkUseCase
import io.github.openflocon.domain.deeplink.usecase.RemoveFromDeeplinkHistoryUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.features.deeplinks.mapper.mapToUi
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkPart
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkViewState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class DeepLinkViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val observeCurrentDeviceDeeplinkUseCase: ObserveCurrentDeviceDeeplinkUseCase,
    private val observeCurrentDeviceDeeplinkHistoryUseCase: ObserveCurrentDeviceDeeplinkHistoryUseCase,
    private val executeDeeplinkUseCase: ExecuteDeeplinkUseCase,
    private val removeFromDeeplinkHistoryUseCase: RemoveFromDeeplinkHistoryUseCase,
) : ViewModel() {

    val deepLinks: StateFlow<List<DeeplinkViewState>> = combines(
        observeCurrentDeviceDeeplinkUseCase(),
        observeCurrentDeviceDeeplinkHistoryUseCase()
    )
        .mapLatest { (deepLinks, history) ->
            mapToUi(
                deepLinks = deepLinks,
                history = history,
            )
        }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

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

            val deeplink = viewState.parts.joinToString(separator = "") {
                when (it) {
                    is DeeplinkPart.Text -> it.value
                    is DeeplinkPart.TextField -> values[it] ?: ""
                }
            }

            executeDeeplinkUseCase(
                deeplink = deeplink,
                deeplinkId = viewState.deeplinkId,
                saveIntoHistory = viewState.deeplinkId == -1L || numberOfTextFields != 0
            )
        }
    }
}
