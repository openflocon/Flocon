package io.github.openflocon.flocondesktop.app.ui.settings

import androidx.lifecycle.ViewModel
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceFloconSdkVersionNameUseCase
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class AboutViewModel(
    observeCurrentDeviceFloconSdkVersionNameUseCase: ObserveCurrentDeviceFloconSdkVersionNameUseCase,
) : ViewModel() {

    /** The SDK version of the selected device + app, null when unknown. */
    val sdkVersion: StateFlow<String?> = observeCurrentDeviceFloconSdkVersionNameUseCase()
        .map { it?.takeIf(String::isNotBlank) }
        .stateInWhileSubscribed(null)
}
