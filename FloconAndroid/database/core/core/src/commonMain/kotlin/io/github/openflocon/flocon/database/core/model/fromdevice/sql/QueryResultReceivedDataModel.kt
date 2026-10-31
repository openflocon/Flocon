package io.github.openflocon.flocon.database.core.model.fromdevice.sql

import kotlinx.serialization.Serializable

@Serializable
internal data class QueryResultDataModel(
    val requestId: String,
    val result: String
)