package io.github.openflocon.flocon.plugins.deeplinks

import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.plugins.deeplinks.model.DeeplinkModel

class FloconDeeplinksConfig : FloconPluginConfig {
    val deeplinks = mutableListOf<DeeplinkModel>()

    fun register(
        link: String,
        label: String? = null,
        description: String? = null,
        block: DeeplinkBuilder.() -> Unit = {}
    ) {
        val builder = DeeplinkBuilder(link, label, description)
        builder.block()
        deeplinks.add(builder.build())
    }

    fun register(link: String) {
        deeplinks.add(DeeplinkModel(link = link, parameters = emptyList()))
    }
}

class DeeplinkBuilder(
    val link: String,
    var label: String? = null,
    var description: String? = null
) {
    private val parameters = mutableListOf<DeeplinkModel.Parameter>()

    fun param(name: String, autoComplete: List<String> = emptyList()) {
        parameters.add(DeeplinkModel.Parameter(name, autoComplete))
    }

    fun build() = DeeplinkModel(
        link = link,
        label = label,
        description = description,
        parameters = parameters
    )
}

interface FloconDeeplinksPlugin : FloconPlugin {
    suspend fun registerDeeplinks(deeplinks: List<DeeplinkModel>)
}