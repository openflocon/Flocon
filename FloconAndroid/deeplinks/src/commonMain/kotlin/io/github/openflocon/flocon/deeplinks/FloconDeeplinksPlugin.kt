package io.github.openflocon.flocon.deeplinks

import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.deeplinks.model.DeeplinkModel

class DeeplinkLinkBuilder internal constructor(
    private val link: String
) {
    private val parameters: MutableMap<String, DeeplinkModel.Parameter> = mutableMapOf()

    var label: String? = null
    var description: String? = null

    infix fun String.withAutoComplete(suggestions: List<String>) {
        parameters[this] = DeeplinkModel.Parameter.AutoComplete(
            name = this,
            autoComplete = suggestions.distinct()
        )
    }

    infix fun String.withVariable(variableName: String) {
        parameters[this] = DeeplinkModel.Parameter.Variable(
            name = this,
            variableName = variableName
        )
    }

    fun build() = DeeplinkModel(
        link = link,
        label = label,
        description = description,
        parameters = parameters.values
            .toList()
    )

}

class DeeplinkVariableBuilder internal constructor(
    private val name: String
) {
    private var mode: DeeplinkVariable.Mode = DeeplinkVariable.Mode.Input

    var description: String? = null

    fun autoComplete(suggestions: List<String>) {
        mode = DeeplinkVariable.Mode.AutoComplete(suggestions)
    }

    internal fun build(): DeeplinkVariable {
        return DeeplinkVariable(
            name = name,
            mode = mode,
            description = description
        )
    }

}

data class DeeplinkVariable(
    val name: String,
    val mode: Mode = Mode.Input,
    val description: String? = null
) {

    sealed interface Mode {
        object Input : Mode
        data class AutoComplete(val suggestions: List<String>) : Mode
    }

}

interface FloconDeeplinksPlugin : FloconPlugin