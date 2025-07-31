package io.github.openflocon.flocondesktop.features.database.data.model.incoming

data class ResponseAndRequestId(
    val requestId: String,
    val response: DatabaseExecuteSqlResponse,
)
