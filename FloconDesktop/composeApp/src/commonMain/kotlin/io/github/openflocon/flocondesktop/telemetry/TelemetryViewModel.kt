package io.github.openflocon.flocondesktop.telemetry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TelemetryViewModel internal constructor(
    private val telemetryManager: TelemetryManager,
    private val appStartedTelemetry: AppStartedTelemetry,
) : ViewModel() {

    init {
        // created once per launch (the consent card lives as long as AppScreen)
        viewModelScope.launch { appStartedTelemetry.send() }
    }

    val state: StateFlow<TelemetryState> = telemetryManager.state

    fun setLevel(level: TelemetryLevel) {
        telemetryManager.setLevel(level)
    }
}
