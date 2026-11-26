package io.github.openflocon.flocondesktop.features.deeplinks.mapper

import io.github.openflocon.domain.deeplink.models.DeeplinkDomainModel
import io.github.openflocon.domain.deeplink.models.DeeplinkSuggestionDomainModel
import io.github.openflocon.domain.deeplink.models.DeeplinkVariableDomainModel
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkPart
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkSuggestionUiModel
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkViewState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

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
    deepLink: DeeplinkDomainModel = DeeplinkDomainModel(
        id = 0,
        label = null,
        link = input,
        description = null,
        parameters = emptyList()
    ),
    variables: List<DeeplinkVariableDomainModel> = emptyList(),
    variableValues: Map<String, String> = emptyMap()
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
                        suggestions = parameter.suggestions.toUi()
                    )

                    is DeeplinkDomainModel.Parameter.Variable -> {
                        val resolved = variableValues[parameter.variableName]
                            ?: variables.firstSuggestionOf(parameter.variableName)
                        DeeplinkPart.Variable(
                            value = resolved ?: "{${parameter.variableName}}",
                            name = parameter.variableName,
                            isResolved = resolved != null,
                        )
                    }
                }
            )
        } else {
            result.add(
                DeeplinkPart.TextField(
                    label = value,
                    suggestions = null
                )
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

private fun List<DeeplinkVariableDomainModel>.firstSuggestionOf(variableName: String): String? = firstOrNull { it.name == variableName }
    ?.mode
    ?.let { it as? DeeplinkVariableDomainModel.Mode.AutoComplete }
    ?.suggestions
    ?.defaultOrFirst()
    ?.value

internal fun List<DeeplinkSuggestionDomainModel>.toUi(): ImmutableList<DeeplinkSuggestionUiModel> = map {
    DeeplinkSuggestionUiModel(value = it.value, label = it.label, isDefault = it.isDefault)
}.toImmutableList()

/** The suggestion the app marked as default, else the first one. */
internal fun List<DeeplinkSuggestionDomainModel>.defaultOrFirst(): DeeplinkSuggestionDomainModel? = firstOrNull { it.isDefault } ?: firstOrNull()

/** What the field displays for [value]: the label of the suggestion with this value, else the value itself. */
internal fun List<DeeplinkSuggestionUiModel>?.displayTextOf(value: String): String = this
    ?.firstOrNull { it.value == value }
    ?.text
    ?: value

/** The value of what the user typed: the value of the suggestion with this label, else the typed text itself. */
internal fun List<DeeplinkSuggestionUiModel>?.valueForInput(text: String): String = this
    ?.firstOrNull { it.label != null && it.label == text }
    ?.value
    ?: text
