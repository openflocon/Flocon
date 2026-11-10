package io.github.openflocon.flocon.database.core

class FloconTransactionState {
    fun track(sql: String): FloconTransactionInfo? = null
    fun onQuery(sql: String): String? = null
}
