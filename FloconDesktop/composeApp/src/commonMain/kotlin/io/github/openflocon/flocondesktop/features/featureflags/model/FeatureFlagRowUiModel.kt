package io.github.openflocon.flocondesktop.features.featureflags.model

import androidx.compose.runtime.Immutable

@Immutable
data class FeatureFlagRowUiModel(
    val id: String,
    val provider: String,
    val name: String,
    val state: FeatureFlagStateUiModel,
    val variant: String,
    val payloadPreview: String?, // single line
)

fun previewFeatureFlagRowUiModel(name: String = "new_checkout") = FeatureFlagRowUiModel(
    id = "firebase/$name",
    provider = "firebase",
    name = name,
    state = FeatureFlagStateUiModel.Custom,
    variant = "treatment",
    payloadPreview = """{"buttonColor":"#FF5722","steps":3}""",
)
