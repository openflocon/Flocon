@file:OptIn(FloconInternalApi::class)

package io.github.openflocon.flocon

import java.util.prefs.Preferences

internal actual fun floconAddressStore(context: FloconContext): FloconAddressStore {
    val preferences = Preferences.userRoot().node("io/github/openflocon/flocon/$ADDRESS_STORE_NAME")
    return object : FloconAddressStore {
        override fun load(): String? = preferences.get(LAST_ADDRESS_KEY, null)

        override fun save(address: String) {
            preferences.put(LAST_ADDRESS_KEY, address)
        }
    }
}
