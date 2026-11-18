package io.github.openflocon.mcp.mocks

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import io.github.openflocon.domain.network.models.httpCode
import io.github.openflocon.mcp.network.toBodyMcpModel

internal const val RESPONSE_TYPE_BODY = "body"
internal const val RESPONSE_TYPE_EXCEPTION = "exception"

internal const val DEFAULT_MEDIA_TYPE = "application/json"

/**
 * Headers describing the transport of the captured body: the mocked body is sent as is, they would be wrong
 */
private val transportHeaders = setOf("content-length", "content-encoding", "transfer-encoding")

/**
 * @param maxBodyChars the body is not returned when null
 */
internal fun MockNetworkDomainModel.toMcpModel(maxBodyChars: Int?) = NetworkMockMcpModel(
    mockId = id,
    displayName = displayName,
    isEnabled = isEnabled,
    isShared = isShared,
    method = expectation.method,
    urlPattern = expectation.urlPattern,
    response = when (val response = response) {
        is MockNetworkDomainModel.Response.Body -> NetworkMockResponseMcpModel(
            type = RESPONSE_TYPE_BODY,
            delayMs = response.delay,
            httpCode = response.httpCode,
            mediaType = response.mediaType,
            headers = response.headers,
            bodyLength = response.body.length,
            body = maxBodyChars?.let { response.body.toBodyMcpModel(it) },
        )

        is MockNetworkDomainModel.Response.Exception -> NetworkMockResponseMcpModel(
            type = RESPONSE_TYPE_EXCEPTION,
            delayMs = response.delay,
            exceptionClassPath = response.classPath,
        )
    },
)

/**
 * Same as the "Create mock" action of the Flocon network screen: the mock matches exactly the url of the call
 * and returns its response
 */
internal fun FloconNetworkCallDomainModel.toMockResponse(): MockNetworkDomainModel.Response.Body = when (val response = response) {
    is FloconNetworkCallDomainModel.Response.Success -> MockNetworkDomainModel.Response.Body(
        httpCode = httpCode() ?: 200,
        body = response.body.orEmpty(),
        mediaType = response.contentType
            ?: response.headers.entries.firstOrNull { it.key.equals("Content-Type", ignoreCase = true) }?.value
            ?: DEFAULT_MEDIA_TYPE,
        delay = 0,
        headers = response.headers.filterKeys { it.lowercase() !in transportHeaders },
    )

    is FloconNetworkCallDomainModel.Response.Failure,
    null,
    -> MockNetworkDomainModel.Response.Body(
        httpCode = 200,
        body = "",
        mediaType = DEFAULT_MEDIA_TYPE,
        delay = 0,
        headers = emptyMap(),
    )
}

internal fun FloconNetworkCallDomainModel.toMockUrlPattern(): String = Regex.escape(request.url)
