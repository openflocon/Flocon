package io.github.openflocon.domain.database.models

data class ResponseAndRequestIdDomainModel(
    val requestId: String,
    val response: DatabaseExecuteSqlResponseDomainModel,
)
