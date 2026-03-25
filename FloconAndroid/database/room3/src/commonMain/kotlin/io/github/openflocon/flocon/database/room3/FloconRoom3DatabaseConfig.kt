package io.github.openflocon.flocon.database.room3

import io.github.openflocon.flocon.database.core.FloconDatabaseConfig
import io.github.openflocon.flocon.dsl.FloconMarker

class FloconRoom3DatabaseConfig internal constructor() {
    internal val paths: MutableList<String> = mutableListOf()

    fun path(path: String) {
        paths.add(path)
    }

}

@OptIn(FloconMarker::class)
fun FloconDatabaseConfig.room(block: FloconRoom3DatabaseConfig.() -> Unit = {}) {
    val config = FloconRoom3DatabaseConfig().apply(block)

    providers.add(
        FloconRoom3DatabaseProviderImpl(
            context = context,
            paths = config.paths
        )
    )
}