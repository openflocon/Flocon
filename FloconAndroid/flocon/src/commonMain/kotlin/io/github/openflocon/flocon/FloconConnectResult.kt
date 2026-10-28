package io.github.openflocon.flocon

/**
 * Result of [Flocon.connect].
 *
 * Whatever the result, the messages kept so far are not lost: call [Flocon.connect] again to retry.
 */
sealed interface FloconConnectResult {
    /** Connected: what was kept has been sent, the next messages are sent right away. */
    data object Connected : FloconConnectResult

    /** Flocon is not started, see `startFlocon`. */
    data object NotStarted : FloconConnectResult

    /** The address could not be read, [reason] tells why. */
    data class InvalidAddress(val reason: String) : FloconConnectResult

    /** The connection attempt failed, [reason] is the error message. */
    data class Failed(val reason: String) : FloconConnectResult
}
