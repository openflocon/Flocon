package io.github.openflocon.flocon

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Does nothing. */
@Suppress("UNUSED_PARAMETER")
fun FloconConfiguration.connectLater(bufferCapacity: Int = 5_000) {
    // no op
}

private val idleState: StateFlow<FloconConnectionState> = MutableStateFlow(FloconConnectionState.Idle)

/** Always [FloconConnectionState.Idle]: nothing is started. */
val Flocon.Companion.connectionState: StateFlow<FloconConnectionState>
    get() = idleState

/** Does nothing, always [FloconConnectResult.NotStarted]. */
@Suppress("UNUSED_PARAMETER")
suspend fun Flocon.Companion.connect(url: String): FloconConnectResult = FloconConnectResult.NotStarted
