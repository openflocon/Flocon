package io.github.openflocon.domain.featureflags.models

data class FeatureFlagDomainModel(
    val provider: String,
    val name: String,
    val variant: String,
    val payload: String?,
)

data class FeatureFlagsProviderDomainModel(
    val provider: String,
    val flags: List<FeatureFlagDomainModel>,
)
