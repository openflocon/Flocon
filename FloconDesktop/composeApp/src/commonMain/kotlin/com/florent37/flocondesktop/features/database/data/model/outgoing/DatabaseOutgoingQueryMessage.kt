package com.florent37.flocondesktop.features.database.data.model.outgoing

import kotlinx.serialization.Serializable

@Serializable
data class DatabaseOutgoingQueryMessage(
    val query: String,
    val requestId: String,
    val database: String,
)
