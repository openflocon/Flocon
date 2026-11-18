package io.github.openflocon.flocondesktop.features.featureflags.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface FeatureFlagProvidersUiModel {
    @Immutable
    data object Empty : FeatureFlagProvidersUiModel

    @Immutable
    data object Loading : FeatureFlagProvidersUiModel

    @Immutable
    data class WithContent(
        val providers: List<String>,
        val selected: String,
    ) : FeatureFlagProvidersUiModel
}

fun previewFeatureFlagProvidersUiModel() = FeatureFlagProvidersUiModel.WithContent(
    providers = listOf("firebase", "statsig"),
    selected = "firebase",
)
