package io.github.openflocon.flocon.plugins.featureflags.model

/**
 * A feature flag as evaluated by the app.
 *
 * @param name the flag key
 * @param variant the evaluated variant, e.g. "control", "treatment", "on", "off"
 * @param payload an optional payload attached to the variant, often json
 */
data class FloconFeatureFlag(
    val name: String,
    val variant: String,
    val payload: String? = null,
)
