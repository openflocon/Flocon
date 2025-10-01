package io.github.openflocon.flocondesktop.features.network.mock.edition.model

import java.util.UUID

data class MockNetworkUiModel(
    val id: String?,
    val isEnabled: Boolean, // not displayed
    val isShared: Boolean, // not displayed
    val expectation: Expectation,
    val response: Response,
) {
    data class Expectation(
        val urlPattern: String, // a regex
        val method: MockNetworkMethodUi,
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

data class EditableMockNetworkUiModel(
    val id: String?,
    val isEnabled: Boolean, // not visible
    val isShared: Boolean,
    val expectation: Expectation,
    val delay: Long,
    val responseType: ResponseType,
    val exceptionResponse: Response.Exception,
    val bodyResponse: Response.Body,
) {
    data class Expectation(
        val urlPattern: String?, // a regex
        val method: MockNetworkMethodUi, // can be get, post, put, ... or a wildcard *
    )

    enum class ResponseType {
        BODY,
        EXCEPTION,
    }

    sealed interface Response {
        data class Body(
            val httpCode: Int,
            val body: String,
            val mediaType: String,
            val headers: List<HeaderUiModel>,
        ) : Response
        data class Exception(
            val classPath: String,
        ) : Response
    }
}

data class HeaderUiModel(
    val id: String = UUID.randomUUID().toString(),
    val key: String,
    val value: String,
)
