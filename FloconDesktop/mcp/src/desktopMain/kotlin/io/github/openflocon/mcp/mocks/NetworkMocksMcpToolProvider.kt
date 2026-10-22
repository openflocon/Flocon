package io.github.openflocon.mcp.mocks

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import io.github.openflocon.domain.network.repository.NetworkMocksRepository
import io.github.openflocon.domain.network.repository.NetworkRepository
import io.github.openflocon.mcp.ARG_DEVICE_ID
import io.github.openflocon.mcp.ARG_PACKAGE_NAME
import io.github.openflocon.mcp.McpTargetArguments
import io.github.openflocon.mcp.McpTargetResolver
import io.github.openflocon.mcp.McpToolException
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.McpToolServer
import io.github.openflocon.mcp.addTypedTool
import io.github.openflocon.mcp.hasExplicitTarget
import io.github.openflocon.mcp.isSameApp
import io.github.openflocon.mcp.readOnlyToolAnnotations
import io.github.openflocon.mcp.writeToolAnnotations
import kotlinx.coroutines.flow.first
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class NetworkMocksMcpToolProvider(
    private val networkMocksRepository: NetworkMocksRepository,
    private val networkRepository: NetworkRepository,
    private val devicesRepository: DevicesRepository,
    private val targetResolver: McpTargetResolver,
) : McpToolProvider {

    override fun register(server: McpToolServer) {
        server.addTypedTool(
            name = "list_network_mocks",
            title = "List network mocks",
            description = "Lists the network mocks configured in Flocon for the mobile app: the requests matching a mock " +
                "receive the mocked response instead of reaching the server. Includes the mocks shared with all the apps. " +
                "Bodies are not returned: use get_network_mock for the details.",
            annotations = readOnlyToolAnnotations,
            handler = ::listMocks,
        )

        server.addTypedTool(
            name = "get_network_mock",
            title = "Get network mock",
            description = "Returns all the details of a network mock, including the mocked response body and headers.",
            annotations = readOnlyToolAnnotations,
            handler = ::getMock,
        )

        server.addTypedTool(
            name = "create_network_mock",
            title = "Create network mock",
            description = "Creates a network mock: the requests of the mobile app matching the url pattern and the method " +
                "will receive the mocked response (or throw the exception) instead of reaching the server. " +
                "Use fromCallId to start from a call captured by Flocon (exact url, method and response), " +
                "then override what you need, ex: only httpCode to simulate a server error. " +
                "The mock is applied immediately when the app is connected to Flocon.",
            annotations = writeToolAnnotations(destructive = false, idempotent = false),
            handler = ::createMock,
        )

        server.addTypedTool(
            name = "update_network_mock",
            title = "Update network mock",
            description = "Updates a network mock: only the given arguments are changed. " +
                "Use enabled to enable / disable it. Giving exceptionClassPath turns it into an exception mock, " +
                "giving a body argument (httpCode, body, mediaType, headers) turns it into a body mock.",
            annotations = writeToolAnnotations(destructive = true, idempotent = true),
            handler = ::updateMock,
        )

        server.addTypedTool(
            name = "delete_network_mock",
            title = "Delete network mock",
            description = "Deletes a network mock. To only stop it temporarily, prefer update_network_mock with enabled = false.",
            annotations = writeToolAnnotations(destructive = true, idempotent = true),
            handler = ::deleteMock,
        )
    }

    private suspend fun listMocks(input: ListNetworkMocksInput): NetworkMocksMcpResult {
        val target = targetResolver.resolve(input)
        return NetworkMocksMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            mocks = networkMocksRepository.observeAll(target).first()
                .filter { !input.onlyEnabled || it.isEnabled }
                .map { it.toMcpModel(maxBodyChars = null) },
        )
    }

    private suspend fun getMock(input: GetNetworkMockInput): NetworkMockMcpModel = requireMock(input.mockId)
        .toMcpModel(maxBodyChars = input.maxBodyChars.coerceIn(100, MAX_BODY_CHARS))

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun createMock(input: CreateNetworkMockInput): NetworkMockChangeMcpResult {
        val (target, call) = when (val callId = input.fromCallId?.takeIf { it.isNotBlank() }) {
            null -> targetResolver.resolve(input) to null
            else -> findCall(input, callId)
        }

        val mock = MockNetworkDomainModel(
            id = Uuid.random().toString(),
            isEnabled = input.enabled,
            expectation = MockNetworkDomainModel.Expectation(
                urlPattern = validUrlPattern(input.urlPattern)
                    ?: call?.toMockUrlPattern()
                    ?: throw McpToolException("Missing 'urlPattern' (or 'fromCallId')"),
                method = validMethod(input.method) ?: call?.request?.method?.uppercase() ?: METHOD_WILDCARD,
            ),
            response = input.toMockResponse(
                base = call?.toMockResponse() ?: MockNetworkDomainModel.Response.Body(
                    httpCode = 200,
                    body = "",
                    mediaType = DEFAULT_MEDIA_TYPE,
                    delay = 0,
                    headers = emptyMap(),
                ),
            ),
            isShared = input.shared,
            displayName = input.displayName.orEmpty(),
        )

        networkMocksRepository.addMock(deviceIdAndPackageName = target, mock = mock)
        val isApplied = sendMocksToConnectedApps(target)

        return NetworkMockChangeMcpResult(
            message = when {
                !mock.isEnabled -> "Mock created, disabled: enable it with update_network_mock."
                isApplied -> "Mock created, it is applied by the app from now on."
                else -> "Mock created, it will be applied when the app connects to Flocon."
            },
            mock = mock.toMcpModel(maxBodyChars = null),
        )
    }

    private suspend fun updateMock(input: UpdateNetworkMockInput): NetworkMockChangeMcpResult {
        val existing = requireMock(input.mockId)
        val owner = ownerOf(input, existing)

        val updated = existing.copy(
            isEnabled = input.enabled ?: existing.isEnabled,
            expectation = MockNetworkDomainModel.Expectation(
                urlPattern = validUrlPattern(input.urlPattern) ?: existing.expectation.urlPattern,
                method = validMethod(input.method) ?: existing.expectation.method,
            ),
            response = input.toMockResponse(base = existing.response),
            isShared = input.shared ?: existing.isShared,
            displayName = input.displayName ?: existing.displayName,
        )

        // the mock is stored by id: it replaces the existing one
        networkMocksRepository.addMock(deviceIdAndPackageName = owner, mock = updated)
        sendMocksToConnectedApps(owner)

        return NetworkMockChangeMcpResult(
            message = "Mock updated.",
            mock = updated.toMcpModel(maxBodyChars = null),
        )
    }

    private suspend fun deleteMock(input: DeleteNetworkMockInput): NetworkMockChangeMcpResult {
        val mock = requireMock(input.mockId)
        networkMocksRepository.deleteMock(id = mock.id)
        sendMocksToConnectedApps(target = null)

        return NetworkMockChangeMcpResult(
            message = "Mock '${mock.id}' deleted.",
            mock = null,
        )
    }

    /**
     * The apps only receive their enabled mocks when they connect: send them the new list
     *
     * @return true if [target] is connected
     */
    private suspend fun sendMocksToConnectedApps(target: DeviceIdAndPackageNameDomainModel?): Boolean {
        val connectedApps = devicesRepository.activeDevices.first()
        connectedApps.forEach { app ->
            networkMocksRepository.setupMocks(
                deviceIdAndPackageName = app,
                mocks = networkMocksRepository.getAllEnabledMocks(app),
            )
        }
        return target != null && connectedApps.any { it.isSameApp(target) }
    }

    /**
     * Mocks of an app are stored with it: when updating, the mock stays on its app unless another one is explicitly given
     */
    private suspend fun ownerOf(
        input: UpdateNetworkMockInput,
        mock: MockNetworkDomainModel,
    ): DeviceIdAndPackageNameDomainModel {
        if (input.hasExplicitTarget()) return targetResolver.resolve(input)

        val targets = targetResolver.allTargets()
        return targets.firstOrNull { target ->
            networkMocksRepository.observeAll(target).first().any { it.id == mock.id && !it.isShared }
        }
            ?: targets.firstOrNull() // shared mock: any app, the current one first
            ?: throw McpToolException("No device / app known by Flocon, pass '$ARG_DEVICE_ID' and '$ARG_PACKAGE_NAME'.")
    }

    private suspend fun findCall(
        input: McpTargetArguments,
        callId: String,
    ): Pair<DeviceIdAndPackageNameDomainModel, FloconNetworkCallDomainModel> {
        // call ids are unique : when no target is given, look on every known app, starting with the current one
        val targets = if (input.hasExplicitTarget()) listOf(targetResolver.resolve(input)) else targetResolver.allTargets()
        for (target in targets) {
            networkRepository.getRequests(target, listOf(callId)).firstOrNull()?.let { return target to it }
        }
        throw McpToolException("Network call '$callId' not found. Use list_network_calls or search_network_calls to get valid ids.")
    }

    private fun MockResponseArguments.toMockResponse(base: MockNetworkDomainModel.Response): MockNetworkDomainModel.Response {
        val httpCode = httpCode?.also { if (it !in 100..599) throw McpToolException("'httpCode' must be between 100 and 599") }
        val delay = delayMs?.also { if (it < 0) throw McpToolException("'delayMs' must be positive") } ?: base.delay
        val body = body
        val headers = headers
        val exceptionClassPath = exceptionClassPath?.takeIf { it.isNotBlank() }
        val mediaType = mediaType?.takeIf { it.isNotBlank() }

        // unlike the other arguments, an empty body is a valid value
        val hasBodyArguments = httpCode != null || body != null || mediaType != null || headers != null
        if (exceptionClassPath != null && hasBodyArguments) {
            throw McpToolException(
                "A mock either throws an exception or returns a body: 'exceptionClassPath' can't be used " +
                    "with 'httpCode', 'body', 'mediaType' or 'headers'",
            )
        }

        return when {
            exceptionClassPath != null -> MockNetworkDomainModel.Response.Exception(
                delay = delay,
                classPath = exceptionClassPath,
            )

            base is MockNetworkDomainModel.Response.Exception && !hasBodyArguments -> base.copy(delay = delay)

            else -> {
                val baseBody = base as? MockNetworkDomainModel.Response.Body
                MockNetworkDomainModel.Response.Body(
                    httpCode = httpCode ?: baseBody?.httpCode ?: 200,
                    body = body ?: baseBody?.body.orEmpty(),
                    mediaType = mediaType ?: baseBody?.mediaType ?: DEFAULT_MEDIA_TYPE,
                    delay = delay,
                    headers = headers ?: baseBody?.headers.orEmpty(),
                )
            }
        }
    }

    private suspend fun requireMock(mockId: String): MockNetworkDomainModel = networkMocksRepository.getMock(mockId)
        ?: throw McpToolException("Mock '$mockId' not found. Use list_network_mocks to get valid ids.")

    private fun validUrlPattern(urlPattern: String?): String? = urlPattern?.takeIf { it.isNotBlank() }?.also {
        try {
            Regex(it)
        } catch (e: IllegalArgumentException) {
            throw McpToolException("'urlPattern' is not a valid regex: ${e.message}")
        }
    }

    private fun validMethod(method: String?): String? = method?.trim()?.uppercase()?.takeIf { it.isNotEmpty() }
}
