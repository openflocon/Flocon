package io.github.openflocon.flocondesktop.telemetry

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

class TelemetryViewModel(
    private val telemetryManager: TelemetryManager,
) : ViewModel() {

    val state: StateFlow<TelemetryState> = telemetryManager.state

    fun setLevel(level: TelemetryLevel) {
        telemetryManager.setLevel(level)
    }
}
