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
    ) {
        fun matches(url: String, method: String): Boolean {
            val urlMatches = pattern.matcher(url).matches()
            val methodMatches = this.method == "*" || this.method.equals(
                method,
                ignoreCase = true
            )
            return urlMatches && methodMatches
        }
    }

    sealed interface Response {
        val delay: Long
        data class Body(
            val httpCode: Int,
            val body: String,
            override val delay: Long,
            val mediaType: String,
            val headers: Map<String, String>,
        ) : Response
        data class ErrorThrow(
            val classPath: String,
            override val delay: Long,
        ) : Response {
            fun generate() : Throwable? {
                val errorClass = Class.forName(classPath)
                return errorClass.newInstance() as? Throwable
            }
        }
    }
}