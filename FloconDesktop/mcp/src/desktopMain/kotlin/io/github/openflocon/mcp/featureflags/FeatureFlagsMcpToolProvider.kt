package io.github.openflocon.mcp.featureflags

import io.github.openflocon.domain.featureflags.models.FeatureFlagDomainModel
import io.github.openflocon.domain.featureflags.repository.FeatureFlagsRepository
import io.github.openflocon.mcp.McpTargetResolver
import io.github.openflocon.mcp.McpToolException
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.addTypedTool
import io.github.openflocon.mcp.readOnlyToolAnnotations
import io.github.openflocon.mcp.McpToolServer

private const val LIST_PAYLOAD_MAX_LENGTH = 200

internal class FeatureFlagsMcpToolProvider(
    private val featureFlagsRepository: FeatureFlagsRepository,
    private val targetResolver: McpTargetResolver,
) : McpToolProvider {

    override fun register(server: McpToolServer) {
        server.addTypedTool(
            name = "list_feature_flags",
            title = "List feature flags",
            description = "Lists the feature flags registered by the mobile app, with their provider (firebase, statsig...), " +
                "their state (on, off or custom), their evaluated variant and a preview of their payload. Can be filtered by provider and by a text " +
                "in the name, variant or payload. Read only.",
            annotations = readOnlyToolAnnotations,
            handler = ::listFeatureFlags,
        )

        server.addTypedTool(
            name = "get_feature_flag",
            title = "Get a feature flag",
            description = "Returns a feature flag of the mobile app with its state, its variant and its full payload (often json).",
            annotations = readOnlyToolAnnotations,
            handler = ::getFeatureFlag,
        )
    }

    private suspend fun listFeatureFlags(input: ListFeatureFlagsInput): FeatureFlagsMcpResult {
        if (input.limit !in 1..FEATURE_FLAGS_MAX_LIMIT) {
            throw McpToolException("limit must be between 1 and $FEATURE_FLAGS_MAX_LIMIT")
        }
        val target = targetResolver.resolve(input)
        val flags = featureFlagsRepository.searchFeatureFlags(
            deviceIdAndPackageName = target,
            provider = input.provider?.takeIf { it.isNotBlank() },
            filter = input.query?.takeIf { it.isNotBlank() },
            limit = input.limit + 1,
        )
        return FeatureFlagsMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            providers = featureFlagsRepository.getProviders(target),
            flags = flags.take(input.limit).map { it.toMcp(truncatePayload = true) },
            hasMore = flags.size > input.limit,
        )
    }

    private suspend fun getFeatureFlag(input: GetFeatureFlagInput): FeatureFlagMcpResult {
        val target = targetResolver.resolve(input)
        val flag = featureFlagsRepository.getFeatureFlag(
            deviceIdAndPackageName = target,
            provider = input.provider,
            name = input.name,
        ) ?: throw McpToolException("Feature flag \"${input.name}\" of provider \"${input.provider}\" not found, see list_feature_flags")

        return FeatureFlagMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            flag = flag.toMcp(truncatePayload = false),
        )
    }

    private fun FeatureFlagDomainModel.toMcp(truncatePayload: Boolean): FeatureFlagMcpModel {
        val truncated = truncatePayload && payload != null && payload!!.length > LIST_PAYLOAD_MAX_LENGTH
        return FeatureFlagMcpModel(
            provider = provider,
            name = name,
            state = state.name.lowercase(),
            variant = variant,
            payload = if (truncated) payload!!.take(LIST_PAYLOAD_MAX_LENGTH) else payload,
            payloadTruncated = truncated,
        )
    }
}
