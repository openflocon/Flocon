package io.github.openflocon.flocon.plugins.network.model

import java.util.regex.Pattern

data class MockNetworkResponse(
    val expectation: Expectation,
    val response: Response,
) {
    data class Expectation(
        val urlPattern: String, // a regex
        val pattern: Pattern,
        val method: String, // can be get, post, put, ... or a wildcard *
    )

    data class Response(
        val httpCode: Int,
        val body: String,
        val mediaType: String,
        val delay: Long,
        val headers: Map<String, String>,
    )
}