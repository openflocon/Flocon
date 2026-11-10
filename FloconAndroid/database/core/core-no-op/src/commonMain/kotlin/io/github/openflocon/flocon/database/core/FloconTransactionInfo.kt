package io.github.openflocon.flocon.database.core

data class FloconTransactionInfo(
    val transactionId: String,
    val nestedTransactionId: String? = null,
    val parentTransactionId: String? = null,
    val depth: Int = 0,
)
