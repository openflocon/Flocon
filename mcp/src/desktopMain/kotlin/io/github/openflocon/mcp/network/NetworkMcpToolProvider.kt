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
import io.github.openflocon.mcp.McpOrder
import io.github.openflocon.mcp.McpTargetResolver
import io.github.openflocon.mcp.McpToolException
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.McpToolServer
import io.github.openflocon.mcp.addTypedTool
import io.github.openflocon.mcp.hasExplicitTarget
import io.github.openflocon.mcp.readOnlyToolAnnotations

internal class NetworkMcpToolProvider(
    private val networkRepository: NetworkRepository,
    private val targetResolver: McpTargetResolver,
    private val generateCurlCommandUseCase: GenerateCurlCommandUseCase,
) : McpToolProvider {

    override fun register(server: McpToolServer) {
        server.addTypedTool(
            name = "list_network_calls",
            title = "List network calls",
            description = "Lists the network calls (HTTP, GraphQL, gRPC, WebSocket events) captured by Flocon on the mobile app, " +
                "newest first. Returns summaries without headers nor bodies: use get_network_call with a callId for the details. " +
                "By default only the current app session is returned (since the last app launch).",
            annotations = readOnlyToolAnnotations,
            handler = ::listNetworkCalls,
        )

        server.addTypedTool(
            name = "get_network_call",
            title = "Get network call",
            description = "Returns all the details of a network call captured by Flocon: request & response headers and bodies, " +
                "GraphQL query, gRPC status, error, timing, and the equivalent cURL command.",
            annotations = readOnlyToolAnnotations,
            handler = ::getNetworkCall,
        )

        server.addTypedTool(
            name = "search_network_calls",
            title = "Search network calls",
            description = "Searches a text in the network calls captured by Flocon (headers, bodies, url, method, status), " +
                "including previous app sessions by default. Returns summaries, newest first.",
            annotations = readOnlyToolAnnotations,
            handler = ::searchNetworkCalls,
        )
    }

    private suspend fun listNetworkCalls(input: ListNetworkCallsInput): NetworkCallsMcpResult {
        val target = targetResolver.resolve(input)
        val methods = input.methods?.map(String::uppercase)

        val calls = getCalls(
            target = target,
            text = input.text,
            includeOldSessions = input.includeOldSessions,
            newestFirst = input.order == McpOrder.NewestFirst,
        ).filter { call ->
            (methods == null || call.request.method.uppercase() in methods) &&
                (input.urlContains == null || call.request.url.contains(input.urlContains, ignoreCase = true)) &&
                (input.statusCodes == null || call.httpCode() in input.statusCodes) &&
                (!input.onlyErrors || call.state() == NetworkCallState.Error || call.state() == NetworkCallState.Exception) &&
                (input.type == null || call.type() == input.type) &&
                (input.sinceTimestamp == null || call.request.startTime >= input.sinceTimestamp)
        }

        return calls.toResult(target = target, limit = input.limit.coerceIn(1, MAX_LIMIT))
    }

    private suspend fun searchNetworkCalls(input: SearchNetworkCallsInput): NetworkCallsMcpResult {
        val target = targetResolver.resolve(input)
        val scopes = input.scopes?.toSet()?.takeIf { it.isNotEmpty() } ?: SearchScope.entries.toSet()

        val calls = getCalls(
            target = target,
            text = null,
            includeOldSessions = input.includeOldSessions,
            newestFirst = true,
        ).filter { it.matches(query = input.query, scope = scopes) }

        return calls.toResult(target = target, limit = input.limit.coerceIn(1, MAX_LIMIT))
    }

    private suspend fun getNetworkCall(input: GetNetworkCallInput): NetworkCallDetailMcpModel {
        // call ids are unique : when no target is given, look on every known app, starting with the current one
        val targets = if (input.hasExplicitTarget()) listOf(targetResolver.resolve(input)) else targetResolver.allTargets()

        for (target in targets) {
            val call = networkRepository.getRequests(target, listOf(input.callId)).firstOrNull() ?: continue
            return call.toDetailMcpModel(
                target = target,
                maxBodyChars = input.maxBodyChars.coerceIn(100, MAX_BODY_CHARS),
                curl = if (input.includeCurl) generateCurlCommandUseCase(call) else null,
            )
        }
        throw McpToolException("Network call '${input.callId}' not found. Use list_network_calls or search_network_calls to get valid ids.")
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
}
