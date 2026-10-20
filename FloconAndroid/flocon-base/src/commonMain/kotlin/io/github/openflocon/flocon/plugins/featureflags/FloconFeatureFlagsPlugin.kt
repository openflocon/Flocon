package io.github.openflocon.flocon.plugins.featureflags

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.plugins.featureflags.builder.FeatureFlagsBuilder
import io.github.openflocon.flocon.plugins.featureflags.model.FloconFeatureFlag

fun floconFeatureFlags(provider: String): FeatureFlagsBuilder {
    return FeatureFlagsBuilder(
        provider = provider,
        featureFlagsPlugin = FloconApp.instance?.client?.featureFlagsPlugin,
    )
}

fun FloconApp.featureFlags(provider: String): FeatureFlagsBuilder {
    return FeatureFlagsBuilder(
        provider = provider,
        featureFlagsPlugin = this.client?.featureFlagsPlugin,
    )
}

interface FloconFeatureFlagsPlugin {
    /**
     * Replaces all the flags known for [provider] with [flags]
     */
    fun registerFeatureFlags(provider: String, flags: List<FloconFeatureFlag>)
}
