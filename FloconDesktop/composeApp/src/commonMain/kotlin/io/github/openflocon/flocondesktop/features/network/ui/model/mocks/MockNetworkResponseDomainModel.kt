package io.github.openflocon.flocondesktop.features.network.ui.model.mocks

import java.util.UUID

data class MockNetworkUiModel(
    val id: String?,
    val isEnabled: Boolean, // not displayed
    val expectation: Expectation,
    val response: Response,
) {
    data class Expectation(
        val urlPattern: String, // a regex
        val method: MockNetworkMethodUi,
    )

    data class Response(
        val httpCode: Int,
        val body: String,
        val mediaType: String,
        val delay: Long,
        val headers: Map<String, String>,
    )
}

data class EditableMockNetworkUiModel(
    val id: String?,
    val isEnabled: Boolean, // not visible
    val expectation: Expectation,
    val response: Response,
) {
    data class Expectation(
        val urlPattern: String?, // a regex
        val method: MockNetworkMethodUi, // can be get, post, put, ... or a wildcard *
    )

    data class Response(
        val httpCode: Int,
        val body: String,
        val mediaType: String,
        val delay: Long,
        val headers: List<HeaderUiModel>,
    )
}

data class HeaderUiModel(
    val id: String = UUID.randomUUID().toString(),
    val key: String,
    val value: String,
)
