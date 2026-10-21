package io.github.openflocon.flocondesktop.features.featureflags

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.copied_to_clipboard
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.featureflags.usecase.ObserveCurrentDeviceFeatureFlagUseCase
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.features.featureflags.mapper.mapToDetailUi
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagDetailUiModel
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagStateUiModel
import io.github.openflocon.library.designsystem.common.copyToClipboard
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class FeatureFlagDetailViewModel(
    provider: String,
    name: String,
    observeCurrentDeviceFeatureFlagUseCase: ObserveCurrentDeviceFeatureFlagUseCase,
    private val feedbackDisplayer: FeedbackDisplayer,
) : ViewModel() {

    val uiState = observeCurrentDeviceFeatureFlagUseCase(provider = provider, name = name)
        .map { it?.mapToDetailUi() }
        .stateInWhileSubscribed(
            default = FeatureFlagDetailUiModel(
                provider = provider,
                name = name,
                state = FeatureFlagStateUiModel.Custom,
                variant = "",
                payload = null,
                rawPayload = null,
                isJsonPayload = false,
            )
        )

    fun onCopyPayload() {
        val payload = uiState.value?.rawPayload ?: return
        viewModelScope.launch {
            copyToClipboard(payload)
            feedbackDisplayer.displayMessage(getString(Res.string.copied_to_clipboard))
        }
    }
}
