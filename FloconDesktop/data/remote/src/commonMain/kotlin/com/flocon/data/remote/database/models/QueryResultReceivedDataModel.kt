package com.flocon.data.remote.database.models

import kotlinx.serialization.Serializable

@Serializable
data class QueryResultReceivedDataModel(
    val requestId: String,
    val result: String,
)
