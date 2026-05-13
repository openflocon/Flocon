package io.github.openflocon.flocon.plugins.deeplinks

import io.github.openflocon.flocon.plugins.deeplinks.model.DeeplinkModel
import io.github.openflocon.flocon.plugins.deeplinks.model.DeeplinkParameterRemote
import io.github.openflocon.flocon.plugins.deeplinks.model.DeeplinkRemote
import io.github.openflocon.flocon.plugins.deeplinks.model.DeeplinkVariableRemote
import io.github.openflocon.flocon.plugins.deeplinks.model.DeeplinksRemote

internal fun createRemote(
    deeplinks: List<DeeplinkModel>,
    variables: List<DeeplinkVariable>
) = DeeplinksRemote(
    deeplinks = deeplinks.map(DeeplinkModel::toRemote),
    variables = variables.map(DeeplinkVariable::toRemote)
)

internal fun DeeplinkModel.toRemote(): DeeplinkRemote = DeeplinkRemote(
    label = label,
    link = link,
    description = description,
    parameters = parameters.map { it.toRemote() }
)

internal fun DeeplinkModel.Parameter.toRemote(): DeeplinkParameterRemote = when (this) {
    is DeeplinkModel.Parameter.AutoComplete -> DeeplinkParameterRemote.AutoComplete(
        name = name,
        autoComplete = autoComplete
    )

    is DeeplinkModel.Parameter.Variable -> DeeplinkParameterRemote.Variable(
        name = name,
        variableName = variableName
    )
}

internal fun DeeplinkVariable.toRemote() = DeeplinkVariableRemote(
    name = name,
    mode = mode.toRemote()
)

internal fun DeeplinkVariable.Mode.toRemote() = when (this) {
    is DeeplinkVariable.Mode.AutoComplete -> DeeplinkVariableRemote.Mode.AutoComplete(
        suggestions = suggestions
    )

    DeeplinkVariable.Mode.Input -> DeeplinkVariableRemote.Mode.Input
}