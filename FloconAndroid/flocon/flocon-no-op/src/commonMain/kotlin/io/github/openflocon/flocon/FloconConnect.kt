package io.github.openflocon.flocon

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Options of [connect], they do nothing. */
class FloconConnectConfig internal constructor() {
    var bufferCapacity: Int = 5_000
    var address: String? = null
    var rememberLastAddress: Boolean = true
    var websocketPort: Int = 9023
    var httpPort: Int = 9024
}

/** Does nothing. */
@Suppress("UNUSED_PARAMETER")
fun FloconConfiguration.connect(block: FloconConnectConfig.() -> Unit = {}) {
    // no op
}

private val idleState: StateFlow<FloconConnectionState> = MutableStateFlow(FloconConnectionState.Idle)

/** Always [FloconConnectionState.Idle]: nothing is started. */
val Flocon.Companion.connectionState: StateFlow<FloconConnectionState>
    get() = idleState

/** Does nothing, always [FloconConnectResult.NotStarted]. */
@Suppress("UNUSED_PARAMETER")
suspend fun Flocon.Companion.connect(address: String): FloconConnectResult = FloconConnectResult.NotStarted
