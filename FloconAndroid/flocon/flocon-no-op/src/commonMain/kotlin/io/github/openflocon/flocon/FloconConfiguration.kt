package io.github.openflocon.flocon

fun startFlocon(
    context: FloconContext,
    block: FloconConfiguration.() -> Unit
) {
    // no op
}

class FloconConfiguration internal constructor() {
    fun <Config : FloconPluginConfig, Plugin : FloconPlugin> install(
        factory: FloconPluginFactory<Config, Plugin>,
        configure: Config.() -> Unit = {}
    ) {
        // no op
    }
}

class FloconConfig internal constructor(
    val context: FloconContext,
)
