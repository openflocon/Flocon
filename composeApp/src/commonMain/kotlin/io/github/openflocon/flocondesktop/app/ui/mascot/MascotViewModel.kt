package io.github.openflocon.flocondesktop.app.ui.mascot

import androidx.lifecycle.ViewModel
import io.github.openflocon.domain.settings.usecase.ObserveMascotEnabledUseCase
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import kotlinx.coroutines.flow.StateFlow

class MascotViewModel(
    observeMascotEnabledUseCase: ObserveMascotEnabledUseCase,
) : ViewModel() {

    val isEnabled: StateFlow<Boolean> = observeMascotEnabledUseCase()
        .stateInWhileSubscribed(true)
}
