package io.github.openflocon.mcp.mocks

import io.github.openflocon.mcp.network.NetworkBodyMcpModel
import kotlinx.serialization.Serializable

@Serializable
internal data class NetworkMocksMcpResult(
    val deviceId: String,
    val packageName: String,
    val mocks: List<NetworkMockMcpModel>,
)

@Serializable
internal data class NetworkMockMcpModel(
    val mockId: String,
    val displayName: String,
    val isEnabled: Boolean,
    val isShared: Boolean,
    val method: String,
    val urlPattern: String,
    val response: NetworkMockResponseMcpModel,
)

@Serializable
internal data class NetworkMockResponseMcpModel(
    val type: String,
    val delayMs: Long,
    val httpCode: Int? = null,
    val mediaType: String? = null,
    val headers: Map<String, String>? = null,
    val bodyLength: Int? = null,
    val body: NetworkBodyMcpModel? = null,
    val exceptionClassPath: String? = null,
)

@Serializable
internal data class NetworkMockChangeMcpResult(
    val message: String,
    val mock: NetworkMockMcpModel?,
)

@Serializable
internal data class WebsocketClientsMcpResult(
    val deviceId: String,
    val packageName: String,
    val websocketIds: List<String>,
    /** explains an empty list */
    val note: String?,
)

@Serializable
internal data class SendWebsocketMessageMcpResult(
    val deviceId: String,
    val packageName: String,
    val websocketId: String,
    val sent: Boolean,
)
