package io.github.openflocon.flocon

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * No-op version of Flocon: nothing is started, no connection is made to the desktop app.
 */
class Flocon private constructor() {
    companion object {
        /** Always [FloconConnectionState.Idle]: nothing is started. */
        val connectionState: StateFlow<FloconConnectionState> = MutableStateFlow(FloconConnectionState.Idle)

        /** Does nothing, always [FloconConnectResult.NotStarted]. */
        suspend fun connect(url: String): FloconConnectResult = FloconConnectResult.NotStarted
    }
}
