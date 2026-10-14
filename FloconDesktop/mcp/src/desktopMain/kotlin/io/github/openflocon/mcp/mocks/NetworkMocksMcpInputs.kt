package io.github.openflocon.mcp.mocks

import io.github.openflocon.mcp.DEVICE_ID_DESCRIPTION
import io.github.openflocon.mcp.McpDescription
import io.github.openflocon.mcp.McpRange
import io.github.openflocon.mcp.McpTargetArguments
import io.github.openflocon.mcp.PACKAGE_NAME_DESCRIPTION
import kotlinx.serialization.Serializable

internal const val METHOD_WILDCARD = "*"
internal const val DEFAULT_MAX_BODY_CHARS = 20_000
internal const val MAX_BODY_CHARS = 200_000

private const val MOCK_ID_DESCRIPTION = "Id of the mock, from list_network_mocks."
private const val URL_PATTERN_DESCRIPTION =
    "Regex that must match the whole url of the request, query included. Ex: \".*/users/[0-9]+\" or \".*/login.*\"."
private const val METHOD_DESCRIPTION = "HTTP method to match, ex: \"GET\", or \"$METHOD_WILDCARD\" for any method."
private const val DISPLAY_NAME_DESCRIPTION = "Name displayed in Flocon."
private const val HTTP_CODE_DESCRIPTION = "HTTP status code of the mocked response. Default 200."
private const val BODY_DESCRIPTION = "Body of the mocked response."
private const val MEDIA_TYPE_DESCRIPTION = "Media type of the mocked response body. Default \"$DEFAULT_MEDIA_TYPE\"."
private const val HEADERS_DESCRIPTION = "Headers of the mocked response, ex: {\"Cache-Control\": \"no-cache\"}."
private const val DELAY_MS_DESCRIPTION = "Delay before the mocked response, in milliseconds. Default 0."
private const val EXCEPTION_CLASS_PATH_DESCRIPTION = "Instead of a response, the request throws this exception. " +
    "Ex: java.net.SocketTimeoutException, java.net.UnknownHostException, java.net.ConnectException, " +
    "javax.net.ssl.SSLHandshakeException, java.io.IOException."

/**
 * Arguments describing the mocked response, a body or an exception
 */
internal interface MockResponseArguments {
    val httpCode: Int?
    val body: String?
    val mediaType: String?
    val headers: Map<String, String>?
    val delayMs: Long?
    val exceptionClassPath: String?
}

@Serializable
internal data class ListNetworkMocksInput(
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
    @McpDescription("Only the enabled mocks. Default false.")
    val onlyEnabled: Boolean = false,
) : McpTargetArguments

@Serializable
internal data class GetNetworkMockInput(
    @McpDescription(MOCK_ID_DESCRIPTION)
    val mockId: String,
    @McpDescription("Bodies longer than this are truncated. Default $DEFAULT_MAX_BODY_CHARS.")
    @McpRange(min = 100, max = MAX_BODY_CHARS)
    val maxBodyChars: Int = DEFAULT_MAX_BODY_CHARS,
)

@Serializable
internal data class CreateNetworkMockInput(
    @McpDescription("Id of a network call (from list_network_calls) used as a template: url, method and response are copied from it.")
    val fromCallId: String? = null,
    @McpDescription(URL_PATTERN_DESCRIPTION)
    val urlPattern: String? = null,
    @McpDescription(METHOD_DESCRIPTION)
    val method: String? = null,
    @McpDescription(DISPLAY_NAME_DESCRIPTION)
    val displayName: String? = null,
    @McpDescription(HTTP_CODE_DESCRIPTION)
    @McpRange(min = 100, max = 599)
    override val httpCode: Int? = null,
    @McpDescription(BODY_DESCRIPTION)
    override val body: String? = null,
    @McpDescription(MEDIA_TYPE_DESCRIPTION)
    override val mediaType: String? = null,
    @McpDescription(HEADERS_DESCRIPTION)
    override val headers: Map<String, String>? = null,
    @McpDescription(DELAY_MS_DESCRIPTION)
    @McpRange(min = 0)
    override val delayMs: Long? = null,
    @McpDescription(EXCEPTION_CLASS_PATH_DESCRIPTION)
    override val exceptionClassPath: String? = null,
    @McpDescription("Whether the mock is active. Default true.")
    val enabled: Boolean = true,
    @McpDescription("Apply the mock to all the devices and apps, not only the targeted one. Default false.")
    val shared: Boolean = false,
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments,
    MockResponseArguments

@Serializable
internal data class UpdateNetworkMockInput(
    @McpDescription(MOCK_ID_DESCRIPTION)
    val mockId: String,
    @McpDescription(URL_PATTERN_DESCRIPTION)
    val urlPattern: String? = null,
    @McpDescription(METHOD_DESCRIPTION)
    val method: String? = null,
    @McpDescription(DISPLAY_NAME_DESCRIPTION)
    val displayName: String? = null,
    @McpDescription(HTTP_CODE_DESCRIPTION)
    @McpRange(min = 100, max = 599)
    override val httpCode: Int? = null,
    @McpDescription(BODY_DESCRIPTION)
    override val body: String? = null,
    @McpDescription(MEDIA_TYPE_DESCRIPTION)
    override val mediaType: String? = null,
    @McpDescription(HEADERS_DESCRIPTION)
    override val headers: Map<String, String>? = null,
    @McpDescription(DELAY_MS_DESCRIPTION)
    @McpRange(min = 0)
    override val delayMs: Long? = null,
    @McpDescription(EXCEPTION_CLASS_PATH_DESCRIPTION)
    override val exceptionClassPath: String? = null,
    @McpDescription("Whether the mock is active.")
    val enabled: Boolean? = null,
    @McpDescription("Apply the mock to all the devices and apps, or only to one app.")
    val shared: Boolean? = null,
    @McpDescription("Moves the mock to this device (see list_devices). By default the mock stays on its app.")
    override val deviceId: String? = null,
    @McpDescription("Moves the mock to this app (see list_devices). By default the mock stays on its app.")
    override val packageName: String? = null,
) : McpTargetArguments,
    MockResponseArguments

@Serializable
internal data class DeleteNetworkMockInput(
    @McpDescription(MOCK_ID_DESCRIPTION)
    val mockId: String,
)
