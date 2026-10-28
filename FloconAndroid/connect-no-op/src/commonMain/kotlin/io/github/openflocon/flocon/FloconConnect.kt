package io.github.openflocon.flocon

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Options of [connectLater], they do nothing. */
class FloconConnectLaterConfig internal constructor() {
    var bufferCapacity: Int = 5_000
    var address: String? = null
    var rememberLastAddress: Boolean = true
}

/** Does nothing. */
@Suppress("UNUSED_PARAMETER")
fun FloconConfiguration.connectLater(block: FloconConnectLaterConfig.() -> Unit = {}) {
    // no op
}

private val idleState: StateFlow<FloconConnectionState> = MutableStateFlow(FloconConnectionState.Idle)

/** Always [FloconConnectionState.Idle]: nothing is started. */
val Flocon.Companion.connectionState: StateFlow<FloconConnectionState>
    get() = idleState

/** Does nothing, always [FloconConnectResult.NotStarted]. */
@Suppress("UNUSED_PARAMETER")
suspend fun Flocon.Companion.connect(address: String): FloconConnectResult = FloconConnectResult.NotStarted
