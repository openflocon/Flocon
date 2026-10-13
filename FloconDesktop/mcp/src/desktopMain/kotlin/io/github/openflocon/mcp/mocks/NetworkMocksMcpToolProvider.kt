package io.github.openflocon.mcp.mocks

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import io.github.openflocon.domain.network.repository.NetworkMocksRepository
import io.github.openflocon.domain.network.repository.NetworkRepository
import io.github.openflocon.mcp.ARG_DEVICE_ID
import io.github.openflocon.mcp.ARG_PACKAGE_NAME
import io.github.openflocon.mcp.McpTargetResolver
import io.github.openflocon.mcp.McpToolException
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.booleanArg
import io.github.openflocon.mcp.booleanProperty
import io.github.openflocon.mcp.intArg
import io.github.openflocon.mcp.integerProperty
import io.github.openflocon.mcp.isSameApp
import io.github.openflocon.mcp.jsonResult
import io.github.openflocon.mcp.longArg
import io.github.openflocon.mcp.properties
import io.github.openflocon.mcp.readOnlyToolAnnotations
import io.github.openflocon.mcp.requiredStringArg
import io.github.openflocon.mcp.safeToolCall
import io.github.openflocon.mcp.stringArg
import io.github.openflocon.mcp.stringMapArg
import io.github.openflocon.mcp.stringMapProperty
import io.github.openflocon.mcp.stringProperty
import io.github.openflocon.mcp.targetProperties
import io.github.openflocon.mcp.writeToolAnnotations
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class NetworkMocksMcpToolProvider(
    private val networkMocksRepository: NetworkMocksRepository,
    private val networkRepository: NetworkRepository,
    private val devicesRepository: DevicesRepository,
    private val targetResolver: McpTargetResolver,
) : McpToolProvider {

    override fun register(server: Server) {
        server.addTool(
            name = "list_network_mocks",
            title = "List network mocks",
            description = "Lists the network mocks configured in Flocon for the mobile app: the requests matching a mock " +
                "receive the mocked response instead of reaching the server. Includes the mocks shared with all the apps. " +
                "Bodies are not returned: use get_network_mock for the details.",
            inputSchema = ToolSchema(
                properties = properties {
                    targetProperties()
                    booleanProperty(ARG_ONLY_ENABLED, "Only the enabled mocks. Default false.")
                },
            ),
            toolAnnotations = readOnlyToolAnnotations,
        ) { request ->
            safeToolCall { jsonResult(listMocks(request)) }
        }

        server.addTool(
            name = "get_network_mock",
            title = "Get network mock",
            description = "Returns all the details of a network mock, including the mocked response body and headers.",
            inputSchema = ToolSchema(
                properties = properties {
                    stringProperty(ARG_MOCK_ID, "Id of the mock, from list_network_mocks.")
                    integerProperty(
                        ARG_MAX_BODY_CHARS,
                        "Bodies longer than this are truncated. Default $DEFAULT_MAX_BODY_CHARS.",
                        minimum = 100,
                        maximum = MAX_BODY_CHARS.toLong(),
                    )
                },
                required = listOf(ARG_MOCK_ID),
            ),
            toolAnnotations = readOnlyToolAnnotations,
        ) { request ->
            safeToolCall { jsonResult(getMock(request)) }
        }

        server.addTool(
            name = "create_network_mock",
            title = "Create network mock",
            description = "Creates a network mock: the requests of the mobile app matching the url pattern and the method " +
                "will receive the mocked response (or throw the exception) instead of reaching the server. " +
                "Use $ARG_FROM_CALL_ID to start from a call captured by Flocon (exact url, method and response), " +
                "then override what you need, ex: only $ARG_HTTP_CODE to simulate a server error. " +
                "The mock is applied immediately when the app is connected to Flocon.",
            inputSchema = ToolSchema(
                properties = properties {
                    stringProperty(
                        ARG_FROM_CALL_ID,
                        "Id of a network call (from list_network_calls) used as a template: url, method and response are copied from it.",
                    )
                    mockProperties()
                    booleanProperty(ARG_ENABLED, "Whether the mock is active. Default true.")
                    booleanProperty(ARG_SHARED, "Apply the mock to all the devices and apps, not only the targeted one. Default false.")
                    targetProperties()
                },
            ),
            toolAnnotations = writeToolAnnotations(destructive = false, idempotent = false),
        ) { request ->
            safeToolCall { jsonResult(createMock(request)) }
        }

        server.addTool(
            name = "update_network_mock",
            title = "Update network mock",
            description = "Updates a network mock: only the given arguments are changed. " +
                "Use $ARG_ENABLED to enable / disable it. Giving $ARG_EXCEPTION_CLASS_PATH turns it into an exception mock, " +
                "giving a body argument ($ARG_HTTP_CODE, $ARG_BODY, $ARG_MEDIA_TYPE, $ARG_HEADERS) turns it into a body mock.",
            inputSchema = ToolSchema(
                properties = properties {
                    stringProperty(ARG_MOCK_ID, "Id of the mock, from list_network_mocks.")
                    mockProperties()
                    booleanProperty(ARG_ENABLED, "Whether the mock is active.")
                    booleanProperty(ARG_SHARED, "Apply the mock to all the devices and apps, or only to one app.")
                    stringProperty(
                        ARG_DEVICE_ID,
                        "Moves the mock to this device (see list_devices). By default the mock stays on its app.",
                    )
                    stringProperty(
                        ARG_PACKAGE_NAME,
                        "Moves the mock to this app (see list_devices). By default the mock stays on its app.",
                    )
                },
                required = listOf(ARG_MOCK_ID),
            ),
            toolAnnotations = writeToolAnnotations(destructive = true, idempotent = true),
        ) { request ->
            safeToolCall { jsonResult(updateMock(request)) }
        }

        server.addTool(
            name = "delete_network_mock",
            title = "Delete network mock",
            description = "Deletes a network mock. To only stop it temporarily, prefer update_network_mock with $ARG_ENABLED = false.",
            inputSchema = ToolSchema(
                properties = properties {
                    stringProperty(ARG_MOCK_ID, "Id of the mock, from list_network_mocks.")
                },
                required = listOf(ARG_MOCK_ID),
            ),
            toolAnnotations = writeToolAnnotations(destructive = true, idempotent = true),
        ) { request ->
            safeToolCall { jsonResult(deleteMock(request)) }
        }
    }

    private suspend fun listMocks(request: CallToolRequest): NetworkMocksMcpResult {
        val target = request.resolveTarget()
        val onlyEnabled = request.booleanArg(ARG_ONLY_ENABLED) ?: false
        return NetworkMocksMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            mocks = networkMocksRepository.observeAll(target).first()
                .filter { !onlyEnabled || it.isEnabled }
                .map { it.toMcpModel(maxBodyChars = null) },
        )
    }

    private suspend fun getMock(request: CallToolRequest): NetworkMockMcpModel {
        val maxBodyChars = (request.intArg(ARG_MAX_BODY_CHARS) ?: DEFAULT_MAX_BODY_CHARS).coerceIn(100, MAX_BODY_CHARS)
        return request.requireMock().toMcpModel(maxBodyChars = maxBodyChars)
    }

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun createMock(request: CallToolRequest): NetworkMockChangeMcpResult {
        val (target, call) = when (val callId = request.stringArg(ARG_FROM_CALL_ID)) {
            null -> request.resolveTarget() to null
            else -> request.findCall(callId)
        }

        val mock = MockNetworkDomainModel(
            id = Uuid.random().toString(),
            isEnabled = request.booleanArg(ARG_ENABLED) ?: true,
            expectation = MockNetworkDomainModel.Expectation(
                urlPattern = request.urlPatternArg()
                    ?: call?.toMockUrlPattern()
                    ?: throw McpToolException("Missing '$ARG_URL_PATTERN' (or '$ARG_FROM_CALL_ID')"),
                method = request.methodArg() ?: call?.request?.method?.uppercase() ?: METHOD_WILDCARD,
            ),
            response = request.toMockResponse(
                base = call?.toMockResponse() ?: MockNetworkDomainModel.Response.Body(
                    httpCode = 200,
                    body = "",
                    mediaType = DEFAULT_MEDIA_TYPE,
                    delay = 0,
                    headers = emptyMap(),
                ),
            ),
            isShared = request.booleanArg(ARG_SHARED) ?: false,
            displayName = request.stringArg(ARG_DISPLAY_NAME).orEmpty(),
        )

        networkMocksRepository.addMock(deviceIdAndPackageName = target, mock = mock)
        val isApplied = sendMocksToConnectedApps(target)

        return NetworkMockChangeMcpResult(
            message = if (isApplied) {
                "Mock created, it is applied by the app from now on."
            } else {
                "Mock created, it will be applied when the app connects to Flocon."
            },
            mock = mock.toMcpModel(maxBodyChars = null),
        )
    }

    private suspend fun updateMock(request: CallToolRequest): NetworkMockChangeMcpResult {
        val existing = request.requireMock()
        val owner = request.ownerOf(existing)

        val updated = existing.copy(
            isEnabled = request.booleanArg(ARG_ENABLED) ?: existing.isEnabled,
            expectation = MockNetworkDomainModel.Expectation(
                urlPattern = request.urlPatternArg() ?: existing.expectation.urlPattern,
                method = request.methodArg() ?: existing.expectation.method,
            ),
            response = request.toMockResponse(base = existing.response),
            isShared = request.booleanArg(ARG_SHARED) ?: existing.isShared,
            displayName = request.stringArg(ARG_DISPLAY_NAME) ?: existing.displayName,
        )

        // the mock is stored by id: it replaces the existing one
        networkMocksRepository.addMock(deviceIdAndPackageName = owner, mock = updated)
        sendMocksToConnectedApps(owner)

        return NetworkMockChangeMcpResult(
            message = "Mock updated.",
            mock = updated.toMcpModel(maxBodyChars = null),
        )
    }

    private suspend fun deleteMock(request: CallToolRequest): NetworkMockChangeMcpResult {
        val mock = request.requireMock()
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
    private suspend fun CallToolRequest.ownerOf(mock: MockNetworkDomainModel): DeviceIdAndPackageNameDomainModel {
        if (hasExplicitTarget()) return resolveTarget()

        val targets = targetResolver.allTargets()
        return targets.firstOrNull { target ->
            networkMocksRepository.observeAll(target).first().any { it.id == mock.id && !it.isShared }
        }
            ?: targets.firstOrNull() // shared mock: any app, the current one first
            ?: throw McpToolException("No device / app known by Flocon, pass '$ARG_DEVICE_ID' and '$ARG_PACKAGE_NAME'.")
    }

    private suspend fun CallToolRequest.findCall(callId: String): Pair<DeviceIdAndPackageNameDomainModel, FloconNetworkCallDomainModel> {
        // call ids are unique : when no target is given, look on every known app, starting with the current one
        val targets = if (hasExplicitTarget()) listOf(resolveTarget()) else targetResolver.allTargets()
        for (target in targets) {
            networkRepository.getRequests(target, listOf(callId)).firstOrNull()?.let { return target to it }
        }
        throw McpToolException("Network call '$callId' not found. Use list_network_calls or search_network_calls to get valid ids.")
    }

    private fun CallToolRequest.toMockResponse(base: MockNetworkDomainModel.Response): MockNetworkDomainModel.Response {
        val exceptionClassPath = stringArg(ARG_EXCEPTION_CLASS_PATH)
        val httpCode = intArg(ARG_HTTP_CODE)?.also {
            if (it !in 100..599) throw McpToolException("'$ARG_HTTP_CODE' must be between 100 and 599")
        }
        val body = bodyArg()
        val mediaType = stringArg(ARG_MEDIA_TYPE)
        val headers = stringMapArg(ARG_HEADERS)
        val delay = longArg(ARG_DELAY_MS)?.also {
            if (it < 0) throw McpToolException("'$ARG_DELAY_MS' must be positive")
        } ?: base.delay

        val hasBodyArguments = httpCode != null || body != null || mediaType != null || headers != null
        if (exceptionClassPath != null && hasBodyArguments) {
            throw McpToolException(
                "A mock either throws an exception or returns a body: '$ARG_EXCEPTION_CLASS_PATH' can't be used " +
                    "with '$ARG_HTTP_CODE', '$ARG_BODY', '$ARG_MEDIA_TYPE' or '$ARG_HEADERS'",
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

    private suspend fun CallToolRequest.requireMock(): MockNetworkDomainModel {
        val mockId = requiredStringArg(ARG_MOCK_ID)
        return networkMocksRepository.getMock(mockId)
            ?: throw McpToolException("Mock '$mockId' not found. Use list_network_mocks to get valid ids.")
    }

    private fun CallToolRequest.urlPatternArg(): String? = stringArg(ARG_URL_PATTERN)?.also {
        try {
            Regex(it)
        } catch (e: IllegalArgumentException) {
            throw McpToolException("'$ARG_URL_PATTERN' is not a valid regex: ${e.message}")
        }
    }

    private fun CallToolRequest.methodArg(): String? = stringArg(ARG_METHOD)?.trim()?.uppercase()

    /**
     * Unlike the other string arguments, an empty body is a valid value
     */
    private fun CallToolRequest.bodyArg(): String? = (arguments?.get(ARG_BODY) as? JsonPrimitive)?.contentOrNull

    private fun CallToolRequest.hasExplicitTarget(): Boolean = stringArg(ARG_DEVICE_ID) != null || stringArg(ARG_PACKAGE_NAME) != null

    private suspend fun CallToolRequest.resolveTarget(): DeviceIdAndPackageNameDomainModel = targetResolver.resolve(
        deviceId = stringArg(ARG_DEVICE_ID),
        packageName = stringArg(ARG_PACKAGE_NAME),
    )

    private fun JsonObjectBuilder.mockProperties() {
        stringProperty(
            ARG_URL_PATTERN,
            "Regex that must match the whole url of the request, query included. Ex: \".*/users/[0-9]+\" or \".*/login.*\".",
        )
        stringProperty(ARG_METHOD, "HTTP method to match, ex: \"GET\", or \"$METHOD_WILDCARD\" for any method.")
        stringProperty(ARG_DISPLAY_NAME, "Name displayed in Flocon.")
        integerProperty(ARG_HTTP_CODE, "HTTP status code of the mocked response. Default 200.", minimum = 100, maximum = 599)
        stringProperty(ARG_BODY, "Body of the mocked response.")
        stringProperty(ARG_MEDIA_TYPE, "Media type of the mocked response body. Default \"$DEFAULT_MEDIA_TYPE\".")
        stringMapProperty(ARG_HEADERS, "Headers of the mocked response, ex: {\"Cache-Control\": \"no-cache\"}.")
        integerProperty(ARG_DELAY_MS, "Delay before the mocked response, in milliseconds. Default 0.", minimum = 0)
        stringProperty(
            ARG_EXCEPTION_CLASS_PATH,
            "Instead of a response, the request throws this exception. Ex: java.net.SocketTimeoutException, " +
                "java.net.UnknownHostException, java.net.ConnectException, javax.net.ssl.SSLHandshakeException, java.io.IOException.",
        )
    }

    private companion object {
        const val ARG_MOCK_ID = "mockId"
        const val ARG_ONLY_ENABLED = "onlyEnabled"
        const val ARG_FROM_CALL_ID = "fromCallId"
        const val ARG_URL_PATTERN = "urlPattern"
        const val ARG_METHOD = "method"
        const val ARG_DISPLAY_NAME = "displayName"
        const val ARG_HTTP_CODE = "httpCode"
        const val ARG_BODY = "body"
        const val ARG_MEDIA_TYPE = "mediaType"
        const val ARG_HEADERS = "headers"
        const val ARG_DELAY_MS = "delayMs"
        const val ARG_EXCEPTION_CLASS_PATH = "exceptionClassPath"
        const val ARG_ENABLED = "enabled"
        const val ARG_SHARED = "shared"
        const val ARG_MAX_BODY_CHARS = "maxBodyChars"

        const val METHOD_WILDCARD = "*"

        const val DEFAULT_MAX_BODY_CHARS = 20_000
        const val MAX_BODY_CHARS = 200_000
    }
}
