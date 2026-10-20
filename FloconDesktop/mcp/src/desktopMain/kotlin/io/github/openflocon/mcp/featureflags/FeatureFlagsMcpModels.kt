package io.github.openflocon.mcp.featureflags

import kotlinx.serialization.Serializable

@Serializable
internal data class FeatureFlagsMcpResult(
    val deviceId: String,
    val packageName: String,
    val providers: List<String>,
    val flags: List<FeatureFlagMcpModel>,
    val hasMore: Boolean,
)

@Serializable
internal data class FeatureFlagMcpResult(
    val deviceId: String,
    val packageName: String,
    val flag: FeatureFlagMcpModel,
)

@Serializable
internal data class FeatureFlagMcpModel(
    val provider: String,
    val name: String,
    val variant: String,
    val payload: String? = null,
    /** true when [payload] was shortened, use get_feature_flag to read it fully */
    val payloadTruncated: Boolean = false,
)
