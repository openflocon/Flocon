package io.github.openflocon.domain.network.models

data class MockNetworkDomainModel(
    val id: String,
    val isEnabled: Boolean,
    val expectation: Expectation,
    val response: Response,
) {
    data class Expectation(
        val urlPattern: String, // a regex
        val method: String, // can be get, post, put, ... or a wildcard *
    )

    sealed interface Response {
        val delay: Long
        data class Body(
            val httpCode: Int,
            val body: String,
            val mediaType: String,
            override val delay: Long,
            val headers: Map<String, String>,
        ) : Response
        data class Exception(
            override val delay: Long,
            val classPath: String,
        ) : Response
    }
}
