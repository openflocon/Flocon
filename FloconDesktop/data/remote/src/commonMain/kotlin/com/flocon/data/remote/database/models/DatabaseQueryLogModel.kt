package com.flocon.data.remote.database.models

import kotlinx.serialization.Serializable

@Serializable
data class DatabaseQueryLogModel(
    val dbId: String,
    val sqlQuery: String,
    val bindArgs: List<String>?,
    val timestamp: Long,
    // the queries of a transaction share the same id, null with an older SDK or outside of a transaction
    val transactionId: String? = null,
)
