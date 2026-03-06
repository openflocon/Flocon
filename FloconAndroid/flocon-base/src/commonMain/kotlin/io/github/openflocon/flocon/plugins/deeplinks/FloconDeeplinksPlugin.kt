package io.github.openflocon.flocon.plugins.deeplinks

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.plugins.deeplinks.model.DeeplinkModel

class DeeplinkLinkBuilder internal constructor(
    private val link: String
) {
    private val parameters: MutableMap<String, DeeplinkModel.Parameter> = mutableMapOf()

    var label: String? = null
    var description: String? = null

    infix fun String.withAutoComplete(suggestions: List<String>) {
        parameters[this] = DeeplinkModel.Parameter.AutoComplete(
            paramName = this,
            suggestions.distinct()
        )
    }

    infix fun String.withVariable(variableName: String) {
        parameters[this] = DeeplinkModel.Parameter.Variable(
            paramName = this,
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

class DeeplinkBuilder {
    private val variables = mutableListOf<DeeplinkVariable>()
    private val deeplinks = mutableListOf<DeeplinkModel>()

    fun variable(name: String, block: DeeplinkVariableBuilder.() -> Unit = {}) {
        val variable = DeeplinkVariableBuilder(name).apply(block)
            .build()

        variables.add(variable)
    }

    fun deeplink(link: String, block: DeeplinkLinkBuilder.() -> Unit = {}) {
        val deeplink = DeeplinkLinkBuilder(link).apply(block)
            .build()

        deeplinks.add(deeplink)
    }

    internal fun deeplinks(): List<DeeplinkModel> = deeplinks.toList()
    internal fun variables(): List<DeeplinkVariable> = variables.toList()
}

fun FloconApp.deeplinks(deeplinksBlock: DeeplinkBuilder.() -> Unit) {
    this.client?.deeplinksPlugin?.let {
        val builder = DeeplinkBuilder().apply(deeplinksBlock)

        it.registerDeeplinks(
            deeplinks = builder.deeplinks(),
            variables = builder.variables()
        )
    }
}

interface FloconDeeplinksPlugin {

    fun registerDeeplinks(
        deeplinks: List<DeeplinkModel>,
        variables: List<DeeplinkVariable>
    )

}