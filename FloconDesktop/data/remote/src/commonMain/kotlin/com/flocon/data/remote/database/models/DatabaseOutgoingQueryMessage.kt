package com.flocon.data.remote.database.models

import kotlinx.serialization.Serializable

@Serializable
data class DatabaseOutgoingQueryMessage(
    val query: String,
    val requestId: String,
    val database: String,
)
