package io.github.openflocon.flocon.plugins.featureflags.builder

import io.github.openflocon.flocon.plugins.featureflags.FloconFeatureFlagsPlugin
import io.github.openflocon.flocon.plugins.featureflags.model.FloconFeatureFlag

class FeatureFlagsBuilder(
    val provider: String,
    private val featureFlagsPlugin: FloconFeatureFlagsPlugin?,
) {
    /**
     * Replaces the flags of this provider with [flags]
     */
    fun register(vararg flags: FloconFeatureFlag) {
        this.register(flags.toList())
    }

    /**
     * Replaces the flags of this provider with [flags]
     */
    fun register(flags: List<FloconFeatureFlag>) {
        featureFlagsPlugin?.registerFeatureFlags(
            provider = provider,
            flags = flags,
        )
    }
}
