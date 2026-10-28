package io.github.openflocon.flocon

fun startFlocon(
    context: FloconContext,
    block: FloconConfiguration.() -> Unit
) {
    // no op
}

class FloconConfiguration internal constructor() {
    /** Does nothing. */
    fun connectLater(bufferCapacity: Int = DEFAULT_BUFFER_CAPACITY) {
        // no op
    }

    fun <Config : FloconPluginConfig, Plugin : FloconPlugin> install(
        factory: FloconPluginFactory<Config, Plugin>,
        configure: Config.() -> Unit = {}
    ) {
        // no op
    }

    internal companion object {
        const val DEFAULT_BUFFER_CAPACITY = 5_000
    }
}

class FloconConfig internal constructor(
    val context: FloconContext,
)
