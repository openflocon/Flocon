package io.github.openflocon.flocon

/**
 * How the app reaches the Flocon desktop.
 */
sealed interface FloconConnectionMode {

    /**
     * Default. Dials the desktop through `adb reverse` (localhost, or 10.0.2.2 on an Android emulator).
     */
    data object Adb : FloconConnectionMode

    /**
     * The app waits for the desktop on a Bluetooth RFCOMM link instead of dialling it. Android only.
     * Pair the phone with the desktop, grant `BLUETOOTH_CONNECT` to the app at runtime, then connect
     * the desktop from Settings > Connection. Experimental, see `docs/bluetooth-exploration.md`.
     */
    data object Bluetooth : FloconConnectionMode

    /**
     * Dials the desktop directly, without adb: a LAN ip, a VPN ip, a public ip or a domain.
     * The desktop listens on its websocket and http ports for all modes at the same time.
     */
    data class Url(
        val host: String,
        val websocketPort: Int = 9023,
        val httpPort: Int = 9024,
    ) : FloconConnectionMode
}
