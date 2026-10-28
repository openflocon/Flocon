@file:OptIn(FloconInternalApi::class)

package io.github.openflocon.flocon

import android.content.Context

internal actual fun floconAddressStore(context: FloconContext): FloconAddressStore {
    val preferences = context.context.getSharedPreferences(ADDRESS_STORE_NAME, Context.MODE_PRIVATE)
    return object : FloconAddressStore {
        override fun load(): String? = preferences.getString(LAST_ADDRESS_KEY, null)

        override fun save(address: String) {
            preferences.edit().putString(LAST_ADDRESS_KEY, address).apply()
        }
    }
}
