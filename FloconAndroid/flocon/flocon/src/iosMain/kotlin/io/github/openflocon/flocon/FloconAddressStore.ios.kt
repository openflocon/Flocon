package io.github.openflocon.flocon

import platform.Foundation.NSUserDefaults

internal actual fun floconAddressStore(context: FloconContext): FloconAddressStore {
    val defaults = NSUserDefaults.standardUserDefaults
    val key = "$ADDRESS_STORE_NAME.$LAST_ADDRESS_KEY"
    return object : FloconAddressStore {
        override fun load(): String? = defaults.stringForKey(key)

        override fun save(address: String) {
            defaults.setObject(address, forKey = key)
        }
    }
}
