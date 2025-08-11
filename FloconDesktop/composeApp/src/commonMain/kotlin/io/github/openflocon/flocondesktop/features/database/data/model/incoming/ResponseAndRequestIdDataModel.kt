package io.github.openflocon.flocondesktop.features.database.data.model.incoming

data class ResponseAndRequestIdDataModel(
    val requestId: String,
    val response: DatabaseExecuteSqlResponseDataModel,
)


