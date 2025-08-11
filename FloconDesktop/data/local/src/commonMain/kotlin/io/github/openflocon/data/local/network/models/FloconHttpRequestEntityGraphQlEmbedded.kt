package io.github.openflocon.data.local.network.models

data class FloconHttpRequestEntityGraphQlEmbedded(
    val query: String,
    val operationType: String,
    val isSuccess: Boolean,
    val responseHttpCode: Int,
)
