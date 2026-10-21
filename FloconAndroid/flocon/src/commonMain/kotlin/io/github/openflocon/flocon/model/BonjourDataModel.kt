package io.github.openflocon.flocon.model

import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.encode
import kotlinx.serialization.Serializable

/**
 * Body of the "bonjour" message sent when the SDK connects to the desktop app.
 * [plugins] are the names of the installed plugins (lowercase, same names as the desktop plugins).
 */
@Serializable
internal class BonjourDataModel(
    val plugins: List<String>,
)

internal fun List<FloconPlugin>.toBonjourBody(encoder: FloconEncoder): String =
    encoder.encode(
        BonjourDataModel(
            plugins = map { it.key.lowercase() }
                .filter { it.isNotEmpty() }
                .distinct(),
        )
    )
