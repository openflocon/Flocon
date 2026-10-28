@file:OptIn(FloconInternalApi::class)

package io.github.openflocon.flocon

@JsFun("(key) => localStorage.getItem(key)")
private external fun getItem(key: String): String?

@JsFun("(key, value) => localStorage.setItem(key, value)")
private external fun setItem(key: String, value: String)

internal actual fun floconAddressStore(context: FloconContext): FloconAddressStore {
    val key = "$ADDRESS_STORE_NAME.$LAST_ADDRESS_KEY"
    return object : FloconAddressStore {
        override fun load(): String? = getItem(key)

        override fun save(address: String) {
            setItem(key, address)
        }
    }
}
