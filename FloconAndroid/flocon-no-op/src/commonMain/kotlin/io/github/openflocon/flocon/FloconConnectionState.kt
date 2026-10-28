package io.github.openflocon.flocon

/**
 * State of the connection to the Flocon desktop app, see `Flocon.connectionState` in `flocon-connect`.
 */
sealed interface FloconConnectionState {

    /** The SDK is not started. */
    data object Idle : FloconConnectionState

    /** Not connected: the messages are kept in memory until the connection is up. */
    data object Buffering : FloconConnectionState

    /** Opening the connection to the desktop app. */
    data object Connecting : FloconConnectionState

    /** Connected: the messages are sent right away. */
    data object Connected : FloconConnectionState

    /** The last attempt to connect failed, for [reason]. The messages are still kept. */
    data class Failed(val reason: String) : FloconConnectionState
}
