package io.github.openflocon.flocon.featureflags.model

/**
 * A feature flag as evaluated by the app.
 *
 * @param name the flag key
 * @param state whether the flag is on, off, or on a custom variant (an experiment arm, a value...)
 * @param variant the evaluated variant, e.g. "control", "treatment", "on", "off"
 * @param payload an optional payload attached to the variant, often json
 */
data class FloconFeatureFlag(
    val name: String,
    val state: FloconFeatureFlagState,
    val variant: String,
    val payload: String? = null,
)

enum class FloconFeatureFlagState {
    On,
    Off,

    /**
     * Neither on nor off: an experiment arm ("treatment", "variant_b"), a value...
     */
    Custom,
}
