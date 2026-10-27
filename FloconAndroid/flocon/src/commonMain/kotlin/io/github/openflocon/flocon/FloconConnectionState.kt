package io.github.openflocon.flocon

/**
 * State of the connection to the Flocon desktop app, see [Flocon.connectionState].
 */
sealed interface FloconConnectionState {

    /** The SDK is not started. */
    data object Idle : FloconConnectionState

    /**
     * Not connected: the messages are kept in memory until the connection is up.
     *
     * This is the state after `startFlocon` with [FloconConfiguration.connectLater] until
     * [Flocon.connect] is called, and right after a connection is lost.
     */
    data object Buffering : FloconConnectionState

    /** Opening the connection to the desktop app. */
    data object Connecting : FloconConnectionState

    /** Connected: the messages are sent right away. */
    data object Connected : FloconConnectionState

    /** The last attempt to connect failed, for [reason]. The messages are still kept. */
    data class Failed(val reason: String) : FloconConnectionState
}
