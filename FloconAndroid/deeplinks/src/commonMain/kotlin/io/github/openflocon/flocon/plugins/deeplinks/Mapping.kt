package io.github.openflocon.flocon.plugins.deeplinks

import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.plugins.deeplinks.model.DeeplinkModel
import io.github.openflocon.flocon.plugins.deeplinks.model.DeeplinkParameterRemote
import io.github.openflocon.flocon.plugins.deeplinks.model.DeeplinkRemote
import io.github.openflocon.flocon.plugins.deeplinks.model.DeeplinksRemote
import kotlinx.serialization.encodeToString

internal fun toDeeplinksJson(deeplinks: List<DeeplinkModel>): String {
    val dto = DeeplinksRemote(deeplinks.map { it.toRemote() })
    return FloconEncoder.json.encodeToString(dto)
}

internal fun DeeplinkModel.toRemote(): DeeplinkRemote = DeeplinkRemote(
    label = label,
    link = link,
    description = description,
    parameters = parameters.map { it.toRemote() }
)

internal fun DeeplinkModel.Parameter.toRemote(): DeeplinkParameterRemote = DeeplinkParameterRemote(
    paramName = paramName,
    autoComplete = autoComplete
)
