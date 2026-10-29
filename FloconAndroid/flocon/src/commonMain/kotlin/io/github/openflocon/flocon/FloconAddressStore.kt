package io.github.openflocon.flocon

/**
 * Where the last desktop address Flocon connected to is kept between two launches of the app.
 */
internal interface FloconAddressStore {
    /** The address saved by [save], or `null`. */
    fun load(): String?

    fun save(address: String)
}

/** Keeps the last address Flocon connected to (SharedPreferences, UserDefaults, ...). */
internal expect fun floconAddressStore(context: FloconContext): FloconAddressStore

internal const val ADDRESS_STORE_NAME = "flocon_connect"
internal const val LAST_ADDRESS_KEY = "last_address"
