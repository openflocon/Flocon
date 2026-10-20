package io.github.openflocon.flocondesktop.features.featureflags.model

import androidx.compose.runtime.Immutable

@Immutable
data class FeatureFlagDetailUiModel(
    val provider: String,
    val name: String,
    val variant: String,
    val payload: String?,
)
