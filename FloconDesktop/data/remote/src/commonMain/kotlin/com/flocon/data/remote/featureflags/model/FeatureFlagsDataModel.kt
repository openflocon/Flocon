package com.flocon.data.remote.featureflags.model

import kotlinx.serialization.Serializable

@Serializable
data class FeatureFlagsDataModel(
    val provider: String,
    val flags: List<FeatureFlagDataModel> = emptyList(),
)

@Serializable
data class FeatureFlagDataModel(
    val name: String,
    val variant: String,
    val payload: String? = null,
)
