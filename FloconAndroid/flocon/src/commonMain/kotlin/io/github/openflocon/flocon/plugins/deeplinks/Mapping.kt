package io.github.openflocon.flocon.plugins.deeplinks

import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.plugins.deeplinks.model.DeeplinkModel
import io.github.openflocon.flocon.plugins.deeplinks.model.DeeplinkParameterRemote
import io.github.openflocon.flocon.plugins.deeplinks.model.DeeplinkRemote
import io.github.openflocon.flocon.plugins.deeplinks.model.DeeplinkVariableRemote
import io.github.openflocon.flocon.plugins.deeplinks.model.DeeplinksRemote

internal fun toDeeplinksJson(
    deeplinks: List<DeeplinkModel>,
    variables: List<DeeplinkVariable>
): String {
    val dto = DeeplinksRemote(
        deeplinks = deeplinks.map(DeeplinkModel::toRemote),
        variables = variables.map(DeeplinkVariable::toRemote)
    )

    return FloconEncoder.json
        .encodeToString(
            serializer = DeeplinksRemote.serializer(),
            value = dto
        )
}

internal fun DeeplinkModel.toRemote(): DeeplinkRemote = DeeplinkRemote(
    label = label,
    link = link,
    description = description,
    parameters = parameters.map(DeeplinkModel.Parameter::toRemote)
)

internal fun DeeplinkVariable.toRemote(): DeeplinkVariableRemote = DeeplinkVariableRemote(
    name = name,
    mode = when (val mode = mode) {
        is DeeplinkVariable.Mode.AutoComplete -> DeeplinkVariableRemote.Mode.AutoComplete(suggestions = mode.suggestions)
        DeeplinkVariable.Mode.Input -> DeeplinkVariableRemote.Mode.Input
    },
    description = description,
)

internal fun DeeplinkModel.Parameter.toRemote(): DeeplinkParameterRemote = when (this) {
    is DeeplinkModel.Parameter.AutoComplete -> DeeplinkParameterRemote.AutoComplete(
        name = paramName,
        autoComplete = autoComplete
    )

    is DeeplinkModel.Parameter.Variable -> DeeplinkParameterRemote.Variable(
        name = paramName,
        variableName = variableName
    )
}

