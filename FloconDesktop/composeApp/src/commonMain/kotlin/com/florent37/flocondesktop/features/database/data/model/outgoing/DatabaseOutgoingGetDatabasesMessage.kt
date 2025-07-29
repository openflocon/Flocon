package com.florent37.flocondesktop.features.database.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DatabaseOutgoingGetDatabasesMessage(
    val query: String,
    val requestId: String,
    val database: String,
)
