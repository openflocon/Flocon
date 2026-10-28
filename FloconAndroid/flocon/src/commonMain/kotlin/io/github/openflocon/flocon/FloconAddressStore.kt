package io.github.openflocon.flocon

/**
 * Where the last desktop address Flocon connected to is kept between two launches of the app.
 * Provided by the optional `flocon-connect` module.
 */
@FloconInternalApi
interface FloconAddressStore {
    /** The address saved by [save], or `null`. */
    fun load(): String?

    fun save(address: String)
}
