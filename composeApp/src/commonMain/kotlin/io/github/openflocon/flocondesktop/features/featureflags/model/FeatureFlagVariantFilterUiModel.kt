package io.github.openflocon.flocondesktop.features.featureflags.model

import androidx.compose.runtime.Immutable

/**
 * A variant present in the flags list (on, off, control, treatment_a...), with its number of flags
 */
@Immutable
data class FeatureFlagVariantFilterUiModel(
    val variant: String,
    val state: FeatureFlagStateUiModel,
    val count: Int,
)
