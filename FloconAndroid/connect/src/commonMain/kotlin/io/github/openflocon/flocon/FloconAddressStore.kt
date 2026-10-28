@file:OptIn(FloconInternalApi::class)

package io.github.openflocon.flocon

/** Keeps the last address Flocon connected to (SharedPreferences, UserDefaults, ...). */
internal expect fun floconAddressStore(context: FloconContext): FloconAddressStore

internal const val ADDRESS_STORE_NAME = "flocon_connect"
internal const val LAST_ADDRESS_KEY = "last_address"
