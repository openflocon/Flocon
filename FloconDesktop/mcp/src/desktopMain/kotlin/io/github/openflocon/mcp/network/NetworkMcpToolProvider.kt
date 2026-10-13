package io.github.openflocon.mcp.network

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.NetworkFilterDomainModel
import io.github.openflocon.domain.network.models.NetworkSortDomainModel
import io.github.openflocon.domain.network.models.SearchScope
import io.github.openflocon.domain.network.models.httpCode
import io.github.openflocon.domain.network.repository.NetworkRepository
import io.github.openflocon.domain.network.usecase.GenerateCurlCommandUseCase
import io.github.openflocon.domain.network.usecase.search.matches
import io.github.openflocon.mcp.ARG_DEVICE_ID
import io.github.openflocon.mcp.ARG_PACKAGE_NAME
import io.github.openflocon.mcp.McpTargetResolver
import io.github.openflocon.mcp.McpToolException
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.arrayProperty
import io.github.openflocon.mcp.booleanArg
import io.github.openflocon.mcp.booleanProperty
import io.github.openflocon.mcp.intArg
import io.github.openflocon.mcp.intListArg
import io.github.openflocon.mcp.integerProperty
import io.github.openflocon.mcp.jsonResult
import io.github.openflocon.mcp.longArg
import io.github.openflocon.mcp.properties
import io.github.openflocon.mcp.readOnlyToolAnnotations
import io.github.openflocon.mcp.requiredStringArg
import io.github.openflocon.mcp.safeToolCall
import io.github.openflocon.mcp.stringArg
import io.github.openflocon.mcp.stringListArg
import io.github.openflocon.mcp.stringProperty
import io.github.openflocon.mcp.targetProperties
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import kotlinx.serialization.json.JsonObjectBuilder

internal class NetworkMcpToolProvider(
    private val networkRepository: NetworkRepository,
    private val targetResolver: McpTargetResolver,
    private val generateCurlCommandUseCase: GenerateCurlCommandUseCase,
) : McpToolProvider {

    override fun register(server: Server) {
        server.addTool(
            name = "list_network_calls",
            title = "List network calls",
            description = "Lists the network calls (HTTP, GraphQL, gRPC, WebSocket events) captured by Flocon on the mobile app, " +
                "newest first. Returns summaries without headers nor bodies: use get_network_call with a callId for the details. " +
                "By default only the current app session is returned (since the last app launch).",
            inputSchema = ToolSchema(
                properties = properties {
                    targetProperties()
                    stringProperty(ARG_TEXT, "Full text filter, matched on url, method, status, duration and request/response bodies.")
                    stringProperty(ARG_URL_CONTAINS, "Only calls whose url contains this text (case insensitive).")
                    arrayProperty(ARG_METHODS, "Only calls with one of these methods, ex: [\"GET\", \"POST\"].", itemType = "string")
                    arrayProperty(ARG_STATUS_CODES, "Only calls with one of these HTTP status codes, ex: [401, 500].", itemType = "integer")
                    booleanProperty(ARG_ONLY_ERRORS, "Only failed calls: exceptions, HTTP codes >= 400, GraphQL errors, gRPC status != OK.")
                    stringProperty(ARG_TYPE, "Only calls of this type.", enum = NetworkCallType.entries.map { it.value })
                    integerProperty(ARG_SINCE_TIMESTAMP, "Only calls started after this epoch timestamp, in milliseconds.")
                    booleanProperty(ARG_INCLUDE_OLD_SESSIONS, "Include calls from previous app launches. Default false.")
                    stringProperty(ARG_ORDER, "Sort order on the start time. Default newest_first.", enum = listOf(ORDER_NEWEST, ORDER_OLDEST))
                    limitProperty()
                },
            ),
            toolAnnotations = readOnlyToolAnnotations,
        ) { request ->
            safeToolCall { jsonResult(listNetworkCalls(request)) }
        }

        server.addTool(
            name = "get_network_call",
            title = "Get network call",
            description = "Returns all the details of a network call captured by Flocon: request & response headers and bodies, " +
                "GraphQL query, gRPC status, error, timing, and the equivalent cURL command.",
            inputSchema = ToolSchema(
                properties = properties {
                    stringProperty(ARG_CALL_ID, "Id of the call, from list_network_calls or search_network_calls.")
                    targetProperties()
                    integerProperty(
                        ARG_MAX_BODY_CHARS,
                        "Bodies longer than this are truncated. Default $DEFAULT_MAX_BODY_CHARS.",
                        minimum = 100,
                        maximum = MAX_BODY_CHARS.toLong(),
                    )
                    booleanProperty(ARG_INCLUDE_CURL, "Include the equivalent cURL command. Default true.")
                },
                required = listOf(ARG_CALL_ID),
            ),
            toolAnnotations = readOnlyToolAnnotations,
        ) { request ->
            safeToolCall { jsonResult(getNetworkCall(request)) }
        }

        server.addTool(
            name = "search_network_calls",
            title = "Search network calls",
            description = "Searches a text in the network calls captured by Flocon (headers, bodies, url, method, status), " +
                "including previous app sessions by default. Returns summaries, newest first.",
            inputSchema = ToolSchema(
                properties = properties {
                    stringProperty(ARG_QUERY, "Text to search, case insensitive.")
                    arrayProperty(
                        ARG_SCOPES,
                        "Where to search. Default: everywhere.",
                        itemType = "string",
                        enum = SearchScope.entries.map { it.name },
                    )
                    targetProperties()
                    booleanProperty(ARG_INCLUDE_OLD_SESSIONS, "Include calls from previous app launches. Default true.")
                    limitProperty()
                },
                required = listOf(ARG_QUERY),
            ),
            toolAnnotations = readOnlyToolAnnotations,
        ) { request ->
            safeToolCall { jsonResult(searchNetworkCalls(request)) }
        }
    }

    private suspend fun listNetworkCalls(request: CallToolRequest): NetworkCallsMcpResult {
        val target = request.resolveTarget()
        val methods = request.stringListArg(ARG_METHODS)?.map(String::uppercase)
        val urlContains = request.stringArg(ARG_URL_CONTAINS)
        val statusCodes = request.intListArg(ARG_STATUS_CODES)
        val onlyErrors = request.booleanArg(ARG_ONLY_ERRORS) ?: false
        val type = request.stringArg(ARG_TYPE)?.let { value ->
            NetworkCallType.entries.firstOrNull { it.value.equals(value, ignoreCase = true) }
                ?: throw McpToolException("Unknown $ARG_TYPE '$value', expected one of ${NetworkCallType.entries.map { it.value }}")
        }
        val since = request.longArg(ARG_SINCE_TIMESTAMP)

        val calls = getCalls(
            target = target,
            text = request.stringArg(ARG_TEXT),
            includeOldSessions = request.booleanArg(ARG_INCLUDE_OLD_SESSIONS) ?: false,
            newestFirst = request.newestFirst(),
        ).filter { call ->
            (methods == null || call.request.method.uppercase() in methods) &&
                (urlContains == null || call.request.url.contains(urlContains, ignoreCase = true)) &&
                (statusCodes == null || call.httpCode() in statusCodes) &&
                (!onlyErrors || call.state() == NetworkCallState.Error || call.state() == NetworkCallState.Exception) &&
                (type == null || call.type() == type) &&
                (since == null || call.request.startTime >= since)
        }

        return calls.toResult(target = target, limit = request.limit())
    }

    private suspend fun searchNetworkCalls(request: CallToolRequest): NetworkCallsMcpResult {
        val target = request.resolveTarget()
        val query = request.requiredStringArg(ARG_QUERY)
        val scopes = request.stringListArg(ARG_SCOPES)
            ?.map { value ->
                SearchScope.entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
                    ?: throw McpToolException("Unknown scope '$value', expected one of ${SearchScope.entries.map { it.name }}")
            }
            ?.toSet()
            ?.takeIf { it.isNotEmpty() }
            ?: SearchScope.entries.toSet()

        val calls = getCalls(
            target = target,
            text = null,
            includeOldSessions = request.booleanArg(ARG_INCLUDE_OLD_SESSIONS) ?: true,
            newestFirst = true,
        ).filter { it.matches(query = query, scope = scopes) }

        return calls.toResult(target = target, limit = request.limit())
    }

    private suspend fun getNetworkCall(request: CallToolRequest): NetworkCallDetailMcpModel {
        val callId = request.requiredStringArg(ARG_CALL_ID)
        val maxBodyChars = (request.intArg(ARG_MAX_BODY_CHARS) ?: DEFAULT_MAX_BODY_CHARS).coerceIn(100, MAX_BODY_CHARS)
        val includeCurl = request.booleanArg(ARG_INCLUDE_CURL) ?: true

        val explicitTarget = request.stringArg(ARG_DEVICE_ID) != null || request.stringArg(ARG_PACKAGE_NAME) != null
        // call ids are unique : when no target is given, look on every known app, starting with the current one
        val targets = if (explicitTarget) listOf(request.resolveTarget()) else targetResolver.allTargets()

        for (target in targets) {
            val call = networkRepository.getRequests(target, listOf(callId)).firstOrNull() ?: continue
            return call.toDetailMcpModel(
                target = target,
                maxBodyChars = maxBodyChars,
                curl = if (includeCurl) generateCurlCommandUseCase(call) else null,
            )
        }
        throw McpToolException("Network call '$callId' not found. Use list_network_calls or search_network_calls to get valid ids.")
    }

    private suspend fun getCalls(
        target: DeviceIdAndPackageNameDomainModel,
        text: String?,
        includeOldSessions: Boolean,
        newestFirst: Boolean,
    ): List<FloconNetworkCallDomainModel> = networkRepository.getRequests(
        deviceIdAndPackageName = target,
        sortedBy = NetworkSortDomainModel(
            column = NetworkSortDomainModel.Column.RequestStartTimeFormatted,
            asc = !newestFirst,
        ),
        filter = NetworkFilterDomainModel(
            filterOnAllColumns = text,
            textsFilters = null,
            methodFilter = null,
            displayOldSessions = includeOldSessions,
        ),
    )

    private fun List<FloconNetworkCallDomainModel>.toResult(
        target: DeviceIdAndPackageNameDomainModel,
        limit: Int,
    ): NetworkCallsMcpResult {
        val returned = take(limit)
        return NetworkCallsMcpResult(
            target = target.toMcpModel(),
            totalMatching = size,
            returned = returned.size,
            calls = returned.map { it.toSummaryMcpModel(currentSessionId = target.appInstance) },
        )
    }

    private suspend fun CallToolRequest.resolveTarget(): DeviceIdAndPackageNameDomainModel = targetResolver.resolve(
        deviceId = stringArg(ARG_DEVICE_ID),
        packageName = stringArg(ARG_PACKAGE_NAME),
    )

    private fun CallToolRequest.limit(): Int = (intArg(ARG_LIMIT) ?: DEFAULT_LIMIT).coerceIn(1, MAX_LIMIT)

    private fun CallToolRequest.newestFirst(): Boolean = when (val order = stringArg(ARG_ORDER)) {
        null, ORDER_NEWEST -> true
        ORDER_OLDEST -> false
        else -> throw McpToolException("Unknown $ARG_ORDER '$order', expected $ORDER_NEWEST or $ORDER_OLDEST")
    }

    private fun JsonObjectBuilder.limitProperty() {
        integerProperty(
            ARG_LIMIT,
            "Maximum number of calls returned. Default $DEFAULT_LIMIT.",
            minimum = 1,
            maximum = MAX_LIMIT.toLong(),
        )
    }

    private companion object {
        const val ARG_CALL_ID = "callId"
        const val ARG_TEXT = "text"
        const val ARG_URL_CONTAINS = "urlContains"
        const val ARG_METHODS = "methods"
        const val ARG_STATUS_CODES = "statusCodes"
        const val ARG_ONLY_ERRORS = "onlyErrors"
        const val ARG_TYPE = "type"
        const val ARG_SINCE_TIMESTAMP = "sinceTimestamp"
        const val ARG_INCLUDE_OLD_SESSIONS = "includeOldSessions"
        const val ARG_ORDER = "order"
        const val ARG_LIMIT = "limit"
        const val ARG_QUERY = "query"
        const val ARG_SCOPES = "scopes"
        const val ARG_MAX_BODY_CHARS = "maxBodyChars"
        const val ARG_INCLUDE_CURL = "includeCurl"

        const val ORDER_NEWEST = "newest_first"
        const val ORDER_OLDEST = "oldest_first"

        const val DEFAULT_LIMIT = 50
        const val MAX_LIMIT = 500
        const val DEFAULT_MAX_BODY_CHARS = 20_000
        const val MAX_BODY_CHARS = 200_000
    }
}
