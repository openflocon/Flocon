package io.github.openflocon.flocon.model

import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.encode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** How the SDK reached the desktop app. */
@Serializable
internal enum class FloconConnectionType {
    /** The default address (localhost, through adb reverse or on a simulator). */
    @SerialName("default")
    Default,

    /** The address given to the connect module. */
    @SerialName("wifi")
    Wifi,
}

/**
 * Body of the "bonjour" message sent when the SDK connects to the desktop app.
 * [plugins] are the names of the installed plugins (lowercase, same names as the desktop plugins).
 * [connectionType] tells how this connection was made.
 */
@Serializable
internal class BonjourDataModel(
    val plugins: List<String>,
    val connectionType: FloconConnectionType,
)

internal fun List<FloconPlugin>.toBonjourBody(
    encoder: FloconEncoder,
    connectionType: FloconConnectionType,
): String =
    encoder.encode(
        BonjourDataModel(
            connectionType = connectionType,
            plugins = map { it.key.lowercase() }
                .filter { it.isNotEmpty() }
                .distinct(),
        )
    )
