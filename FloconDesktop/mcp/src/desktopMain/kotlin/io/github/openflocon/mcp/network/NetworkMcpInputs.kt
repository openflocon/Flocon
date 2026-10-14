package io.github.openflocon.mcp.network

import io.github.openflocon.domain.network.models.SearchScope
import io.github.openflocon.mcp.DEVICE_ID_DESCRIPTION
import io.github.openflocon.mcp.McpDescription
import io.github.openflocon.mcp.McpOrder
import io.github.openflocon.mcp.McpTargetArguments
import io.github.openflocon.mcp.PACKAGE_NAME_DESCRIPTION
import kotlinx.serialization.Serializable

internal const val DEFAULT_LIMIT = 50
internal const val MAX_LIMIT = 500
internal const val DEFAULT_MAX_BODY_CHARS = 20_000
internal const val MAX_BODY_CHARS = 200_000

@Serializable
internal data class ListNetworkCallsInput(
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
    @McpDescription("Full text filter, matched on url, method, status, duration and request/response bodies.")
    val text: String? = null,
    @McpDescription("Only calls whose url contains this text (case insensitive).")
    val urlContains: String? = null,
    @McpDescription("Only calls with one of these methods, ex: [\"GET\", \"POST\"].")
    val methods: List<String>? = null,
    @McpDescription("Only calls with one of these HTTP status codes, ex: [401, 500].")
    val statusCodes: List<Int>? = null,
    @McpDescription("Only failed calls: exceptions, HTTP codes >= 400, GraphQL errors, gRPC status != OK.")
    val onlyErrors: Boolean = false,
    @McpDescription("Only calls of this type.")
    val type: NetworkCallType? = null,
    @McpDescription("Only calls started after this epoch timestamp, in milliseconds.")
    val sinceTimestamp: Long? = null,
    @McpDescription("Include calls from previous app launches. Default false.")
    val includeOldSessions: Boolean = false,
    @McpDescription("Sort order on the start time. Default newest_first.")
    val order: McpOrder = McpOrder.NewestFirst,
    @McpDescription("Maximum number of calls returned, from 1 to $MAX_LIMIT. Default $DEFAULT_LIMIT.")
    val limit: Int = DEFAULT_LIMIT,
) : McpTargetArguments

@Serializable
internal data class GetNetworkCallInput(
    @McpDescription("Id of the call, from list_network_calls or search_network_calls.")
    val callId: String,
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
    @McpDescription("Bodies longer than this are truncated, from 100 to $MAX_BODY_CHARS. Default $DEFAULT_MAX_BODY_CHARS.")
    val maxBodyChars: Int = DEFAULT_MAX_BODY_CHARS,
    @McpDescription("Include the equivalent cURL command. Default true.")
    val includeCurl: Boolean = true,
) : McpTargetArguments

@Serializable
internal data class SearchNetworkCallsInput(
    @McpDescription("Text to search, case insensitive.")
    val query: String,
    @McpDescription("Where to search. Default: everywhere.")
    val scopes: List<SearchScope>? = null,
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
    @McpDescription("Include calls from previous app launches. Default true.")
    val includeOldSessions: Boolean = true,
    @McpDescription("Maximum number of calls returned, from 1 to $MAX_LIMIT. Default $DEFAULT_LIMIT.")
    val limit: Int = DEFAULT_LIMIT,
) : McpTargetArguments
