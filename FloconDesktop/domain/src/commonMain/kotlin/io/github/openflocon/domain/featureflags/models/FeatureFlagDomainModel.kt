package io.github.openflocon.domain.featureflags.models

data class FeatureFlagDomainModel(
    val provider: String,
    val name: String,
    val state: FeatureFlagStateDomainModel,
    val variant: String,
    val payload: String?,
)

enum class FeatureFlagStateDomainModel(val names: Set<String>) {
    On(setOf("on", "treatment")),
    Off(setOf("off", "control")),
    Custom(emptySet()), // an experiment arm, a value...
}

data class FeatureFlagsProviderDomainModel(
    val provider: String,
    val flags: List<FeatureFlagDomainModel>,
)
