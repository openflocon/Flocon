package io.github.openflocon.flocondesktop.features.deeplinks.mapper

import io.github.openflocon.domain.deeplink.models.DeeplinkDomainModel
import io.github.openflocon.domain.deeplink.models.DeeplinkVariableDomainModel
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkPart
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkViewState

data class DeeplinkItem(
    val model: DeeplinkDomainModel,
    val isHistory: Boolean,
)

internal fun mapToUi(
    history: List<DeeplinkDomainModel>,
    deepLinks: List<DeeplinkDomainModel>,
    variables: List<DeeplinkVariableDomainModel>,
    variableValues: Map<String, String>
): List<DeeplinkViewState> = buildList {
    addAll(history.map { DeeplinkItem(model = it, isHistory = true) })
    addAll(deepLinks.map { DeeplinkItem(model = it, isHistory = false) })
}
    .distinctBy { it.model.link }
    .map {
        mapToUi(
            deepLink = it.model,
            isHistory = it.isHistory,
            variables = variables,
            variableValues = variableValues
        )
    }

internal fun mapToUi(
    deepLink: DeeplinkDomainModel,
    isHistory: Boolean,
    variables: List<DeeplinkVariableDomainModel>,
    variableValues: Map<String, String>
): DeeplinkViewState = DeeplinkViewState(
    label = deepLink.label,
    description = deepLink.description,
    deeplinkId = deepLink.id,
    isHistory = isHistory,
    parts = if (isHistory) {
        listOf(DeeplinkPart.Text(deepLink.link))
    } else {
        parseDeeplinkString(
            input = deepLink.link,
            deepLink = deepLink,
            variables = variables,
            variableValues = variableValues
        )
    }
)

internal fun parseDeeplinkString(
    input: String,
    deepLink: DeeplinkDomainModel,
    variables: List<DeeplinkVariableDomainModel>,
    variableValues: Map<String, String>
): List<DeeplinkPart> {
    val regex = "\\[([^\\[\\]]*)\\]".toRegex() // Regex pour trouver [quelquechose]
    val result = mutableListOf<DeeplinkPart>()
    var lastIndex = 0

    regex.findAll(input).forEach { matchResult ->
        val range = matchResult.range
        val value = matchResult.groupValues[1] // Le contenu entre les crochets

        // 1. Ajouter la partie "Text" avant le [value]
        if (range.first > lastIndex) {
            val textContent = input.substring(lastIndex, range.first)
            if (textContent.isNotEmpty()) {
                result.add(DeeplinkPart.Text(textContent))
            }
        }

        // 2. Ajouter la partie "TextField"
        val parameter = deepLink.parameters.find { it.name == value }

        if (parameter != null) {
            result.add(
                when (parameter) {
                    is DeeplinkDomainModel.Parameter.AutoComplete -> DeeplinkPart.TextField(
                        label = value,
                        autoComplete = parameter.autoComplete
                    )

                    is DeeplinkDomainModel.Parameter.Variable -> DeeplinkPart.Variable(
                        value = variableValues[parameter.variableName]
                            ?: variables.firstSuggestionOf(parameter.variableName)
                            ?: "{${parameter.variableName}}"
                    )
                }
            )
        }

        lastIndex = range.last + 1
    }

    // 3. Ajouter la dernière partie "Text" après le dernier [value] (s'il y en a une)
    if (lastIndex < input.length) {
        val remainingText = input.substring(lastIndex)
        if (remainingText.isNotEmpty()) {
            result.add(DeeplinkPart.Text(remainingText))
        }
    }

    return result
}

private fun List<DeeplinkVariableDomainModel>.firstSuggestionOf(variableName: String): String? =
    firstOrNull { it.name == variableName }
        ?.mode
        ?.let { it as? DeeplinkVariableDomainModel.Mode.AutoComplete }
        ?.suggestions
        ?.firstOrNull()
