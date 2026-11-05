package io.github.openflocon.flocondesktop.app.ui.settings

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.models.settings.ServerPortsDomainModel
import io.github.openflocon.domain.settings.usecase.ServerPortsError

@Immutable
data class ServerPortsFormUiModel(
    val websocketPort: String,
    val httpPort: String,
    val error: ServerPortsError? = null,
    // forced by a launch option for this session: read only, never saved
    val websocketPortOverridden: Boolean = false,
    val httpPortOverridden: Boolean = false,
) {
    val hasOverride: Boolean
        get() = websocketPortOverridden || httpPortOverridden

    /** There is something to reset: a port that is not forced by a launch option and is not the default one. */
    val canReset: Boolean
        get() = ServerPortsDomainModel().let { defaults ->
            (!websocketPortOverridden && websocketPort != defaults.websocketPort.toString()) ||
                (!httpPortOverridden && httpPort != defaults.httpPort.toString())
        }
}

fun previewServerPortsFormUiModel(
    error: ServerPortsError? = null,
    websocketPortOverridden: Boolean = false,
    websocketPort: String = "9023",
    httpPort: String = "9024",
) = ServerPortsFormUiModel(
    websocketPort = websocketPort,
    httpPort = httpPort,
    error = error,
    websocketPortOverridden = websocketPortOverridden,
)
