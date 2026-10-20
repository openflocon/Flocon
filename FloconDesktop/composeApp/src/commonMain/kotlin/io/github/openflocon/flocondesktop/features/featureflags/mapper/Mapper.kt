package io.github.openflocon.flocondesktop.features.featureflags.mapper

import io.github.openflocon.domain.featureflags.models.FeatureFlagDomainModel
import io.github.openflocon.flocondesktop.common.ui.JsonPrettyPrinter
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagDetailUiModel
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagRowUiModel

internal fun FeatureFlagDomainModel.mapToUi() = FeatureFlagRowUiModel(
    id = "$provider/$name",
    provider = provider,
    name = name,
    variant = variant,
    hasPayload = !payload.isNullOrEmpty(),
)

internal fun FeatureFlagDomainModel.mapToDetailUi() = FeatureFlagDetailUiModel(
    provider = provider,
    name = name,
    variant = variant,
    payload = payload?.takeIf { it.isNotEmpty() }?.let { JsonPrettyPrinter.prettyPrint(it) },
)
