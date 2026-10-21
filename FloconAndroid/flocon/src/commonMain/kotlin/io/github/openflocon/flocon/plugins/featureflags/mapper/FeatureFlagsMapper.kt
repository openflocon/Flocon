package io.github.openflocon.flocon.plugins.featureflags.mapper

import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.plugins.featureflags.model.FloconFeatureFlag
import io.github.openflocon.flocon.plugins.featureflags.model.FloconFeatureFlagState
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

internal fun featureFlagsToJson(provider: String, flags: List<FloconFeatureFlag>): String {
    return FloconEncoder.json.encodeToString(
        FeatureFlagsSerializable(
            provider = provider,
            flags = flags.map { it.toSerializable() },
        )
    )
}

@Serializable
internal class FeatureFlagsSerializable(
    val provider: String,
    val flags: List<FeatureFlagSerializable>,
)

@Serializable
internal class FeatureFlagSerializable(
    val name: String,
    val state: String,
    val variant: String,
    val payload: String? = null,
)

internal fun FloconFeatureFlag.toSerializable(): FeatureFlagSerializable {
    return FeatureFlagSerializable(
        name = name,
        state = state.toRemote(),
        variant = variant,
        payload = payload,
    )
}

private fun FloconFeatureFlagState.toRemote(): String = when (this) {
    FloconFeatureFlagState.On -> "on"
    FloconFeatureFlagState.Off -> "off"
    FloconFeatureFlagState.Custom -> "custom"
}
