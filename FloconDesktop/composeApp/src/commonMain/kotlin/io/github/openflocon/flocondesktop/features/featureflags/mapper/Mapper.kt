package io.github.openflocon.flocondesktop.features.featureflags.mapper

import io.github.openflocon.domain.featureflags.models.FeatureFlagDomainModel
import io.github.openflocon.domain.featureflags.models.FeatureFlagStateDomainModel
import io.github.openflocon.flocondesktop.common.ui.JsonPrettyPrinter
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagDetailUiModel
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagRowUiModel
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagStateUiModel

internal fun FeatureFlagDomainModel.mapToUi() = FeatureFlagRowUiModel(
    id = "$provider/$name",
    provider = provider,
    name = name,
    state = state.mapToUi(),
    variant = variant,
    payloadPreview = payload?.takeIf { it.isNotBlank() }?.lineSequence()?.joinToString(" ") { it.trim() },
)

internal fun FeatureFlagDomainModel.mapToDetailUi() = FeatureFlagDetailUiModel(
    provider = provider,
    name = name,
    state = state.mapToUi(),
    variant = variant,
    payload = payload?.takeIf { it.isNotEmpty() }?.let { JsonPrettyPrinter.prettyPrint(it) },
)

private fun FeatureFlagStateDomainModel.mapToUi() = when (this) {
    FeatureFlagStateDomainModel.On -> FeatureFlagStateUiModel.On
    FeatureFlagStateDomainModel.Off -> FeatureFlagStateUiModel.Off
    FeatureFlagStateDomainModel.Custom -> FeatureFlagStateUiModel.Custom
}
