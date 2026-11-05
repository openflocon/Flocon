package io.github.openflocon.flocondesktop.app.ui.settings

import androidx.compose.runtime.Immutable
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
}

fun previewServerPortsFormUiModel(
    error: ServerPortsError? = null,
    websocketPortOverridden: Boolean = false,
) = ServerPortsFormUiModel(
    websocketPort = "9023",
    httpPort = "9024",
    error = error,
    websocketPortOverridden = websocketPortOverridden,
)
