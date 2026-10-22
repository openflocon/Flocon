package io.github.openflocon.flocondesktop.features.featureflags.mapper

import io.github.openflocon.domain.featureflags.models.FeatureFlagDomainModel
import io.github.openflocon.domain.featureflags.models.FeatureFlagStateDomainModel
import io.github.openflocon.flocondesktop.common.ui.JsonPrettyPrinter
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagDetailUiModel
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagRowUiModel
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagStateUiModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

internal fun FeatureFlagDomainModel.mapToUi() = FeatureFlagRowUiModel(
    id = "$provider/$name",
    provider = provider,
    name = name,
    state = state.mapToUi(),
    variant = variant,
    payloadPreview = payload?.takeIf { it.isNotBlank() }?.lineSequence()?.joinToString(" ") { it.trim() },
)

internal fun FeatureFlagDomainModel.mapToDetailUi(): FeatureFlagDetailUiModel {
    val rawPayload = payload?.takeIf { it.isNotBlank() }
    val isJson = rawPayload != null && isJsonObjectOrArray(rawPayload)
    return FeatureFlagDetailUiModel(
        provider = provider,
        name = name,
        state = state.mapToUi(),
        variant = variant,
        payload = if (isJson) JsonPrettyPrinter.prettyPrint(rawPayload) else rawPayload,
        rawPayload = rawPayload,
        isJsonPayload = isJson,
    )
}

private fun isJsonObjectOrArray(text: String): Boolean = try {
    Json.parseToJsonElement(text).let { it is JsonObject || it is JsonArray }
} catch (t: Throwable) {
    false
}

private fun FeatureFlagStateDomainModel.mapToUi() = when (this) {
    FeatureFlagStateDomainModel.On -> FeatureFlagStateUiModel.On
    FeatureFlagStateDomainModel.Off -> FeatureFlagStateUiModel.Off
    FeatureFlagStateDomainModel.Custom -> FeatureFlagStateUiModel.Custom
}
