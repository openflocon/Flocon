package io.github.openflocon.flocondesktop.features.featureflags

import androidx.lifecycle.ViewModel
import io.github.openflocon.domain.featureflags.usecase.ObserveCurrentDeviceFeatureFlagUseCase
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.features.featureflags.mapper.mapToDetailUi
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagDetailUiModel
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagStateUiModel
import kotlinx.coroutines.flow.map

class FeatureFlagDetailViewModel(
    provider: String,
    name: String,
    observeCurrentDeviceFeatureFlagUseCase: ObserveCurrentDeviceFeatureFlagUseCase,
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
            )
        )
}
