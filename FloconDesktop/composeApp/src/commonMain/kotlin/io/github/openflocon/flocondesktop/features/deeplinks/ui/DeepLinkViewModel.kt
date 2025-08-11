package io.github.openflocon.flocondesktop.features.deeplinks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.flocondesktop.common.ui.feedback.FeedbackDisplayer
import io.github.openflocon.domain.deeplink.usecase.ExecuteDeeplinkUseCase
import io.github.openflocon.domain.deeplink.usecase.ObserveCurrentDeviceDeeplinkUseCase
import io.github.openflocon.flocondesktop.features.deeplinks.ui.mapper.mapToUi
import io.github.openflocon.flocondesktop.features.deeplinks.ui.model.DeeplinkPart
import io.github.openflocon.flocondesktop.features.deeplinks.ui.model.DeeplinkViewState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DeepLinkViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val observeCurrentDeviceDeeplinkUseCase: ObserveCurrentDeviceDeeplinkUseCase,
    private val executeDeeplinkUseCase: ExecuteDeeplinkUseCase,
) : ViewModel() {

    val deepLinks: StateFlow<List<DeeplinkViewState>> = observeCurrentDeviceDeeplinkUseCase()
        .map { deepLinks -> mapToUi(deepLinks) }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    fun submit(viewState: DeeplinkViewState, values: Map<DeeplinkPart.TextField, String>) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            if (viewState.parts.count { it is DeeplinkPart.TextField } != values.values.filterNot { it.isBlank() }.size) {
                feedbackDisplayer.displayMessage(
                    "All deeplink parts should be filled",
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

            executeDeeplinkUseCase(deeplink = deeplink)
        }
    }
}
