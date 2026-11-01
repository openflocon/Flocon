package io.github.openflocon.flocon.featureflags.mapper

import io.github.openflocon.flocon.featureflags.model.FloconFeatureFlag
import io.github.openflocon.flocon.featureflags.model.FloconFeatureFlagState
import kotlinx.serialization.Serializable

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
