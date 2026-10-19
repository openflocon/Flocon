package io.github.openflocon.mcp.images

import kotlinx.serialization.Serializable

@Serializable
internal data class ImagesMcpResult(
    val deviceId: String,
    val packageName: String,
    val returned: Int,
    val hasMore: Boolean,
    val images: List<ImageMcpModel>,
)

@Serializable
internal data class ImageMcpModel(
    val url: String,
    val timestamp: String,
    val timestampMs: Long,
    val headers: Map<String, String>? = null,
)
