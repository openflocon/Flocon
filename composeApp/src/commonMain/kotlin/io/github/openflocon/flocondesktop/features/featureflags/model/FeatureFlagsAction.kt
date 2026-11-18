package io.github.openflocon.flocondesktop.features.featureflags.model

sealed interface FeatureFlagsAction {
    data class OnClick(val item: FeatureFlagRowUiModel) : FeatureFlagsAction
}
