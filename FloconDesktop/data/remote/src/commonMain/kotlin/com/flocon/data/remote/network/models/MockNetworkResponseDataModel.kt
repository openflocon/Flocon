package com.flocon.data.remote.network.models

import kotlinx.serialization.Serializable

@Serializable
data class MockNetworkResponseDataModel(
    val expectation: Expectation,
    val response: Response,
) {
    @Serializable
    data class Expectation(
        val urlPattern: String, // a regex
        val method: String, // can be get, post, put, ... or a wildcard *
    )

    @Serializable
    data class Response(
        val httpCode: Int,
        val body: String,
        val mediaType: String,
        val delay: Long,
        val headers: Map<String, String>,
    )
}
