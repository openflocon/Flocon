package io.github.openflocon.flocondesktop.features.database.data.model.incoming

import kotlinx.serialization.Serializable

@Serializable
data class QueryResultReceivedDataModel(
    val requestId: String,
    val result: String,
)
