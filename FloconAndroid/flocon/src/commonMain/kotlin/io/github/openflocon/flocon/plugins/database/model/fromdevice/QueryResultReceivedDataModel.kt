package io.github.openflocon.flocon.plugins.database.model.fromdevice

import kotlinx.serialization.Serializable

@Serializable
internal data class QueryResultDataModel(
    val requestId: String,
    val result: String,
)