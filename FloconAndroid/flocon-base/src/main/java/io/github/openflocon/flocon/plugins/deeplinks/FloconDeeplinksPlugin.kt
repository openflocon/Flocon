package io.github.openflocon.flocon.plugins.deeplinks

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.plugins.deeplinks.model.DeeplinkModel

class ParameterBuilder {
    val parameters: MutableMap<String, DeeplinkModel.Parameter> = mutableMapOf()

    infix fun String.withAutoComplete(suggestions: List<String>) {
        parameters[this] = DeeplinkModel.Parameter(paramName = this, suggestions.distinct())
    }

    fun build() : List<DeeplinkModel.Parameter> {
        return parameters.values.toList()
    }
}

class DeeplinkBuilder {
    private val deeplinks = mutableListOf<DeeplinkModel>()

    fun deeplink(
        link: String,
        label: String? = null,
        description: String? = null,
        parameters: (ParameterBuilder.() -> Unit)? = null,
    ) {
        deeplinks.add(
            DeeplinkModel(
                link = link,
                label = label,
                description = description,
                parameters = parameters?.let { ParameterBuilder().apply(parameters).build() } ?: emptyList()
            )
        )
    }

    fun build(): List<DeeplinkModel> {
        return deeplinks.toList()
    }
}

fun FloconApp.deeplinks(deeplinksBlock: DeeplinkBuilder.() -> Unit) {
    this.client?.deeplinksPlugin?.let {
        it.registerDeeplinks(DeeplinkBuilder().apply(deeplinksBlock).build())
    }
}

interface FloconDeeplinksPlugin : FloconPlugin {
    fun registerDeeplinks(deeplinks: List<DeeplinkModel>)
}