package io.github.openflocon.flocondesktop.app.ui.settings

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.settings.usecase.ServerPortsError

@Immutable
data class ServerPortsFormUiModel(
    val websocketPort: String,
    val httpPort: String,
    val error: ServerPortsError? = null,
)

fun previewServerPortsFormUiModel(error: ServerPortsError? = null) = ServerPortsFormUiModel(
    websocketPort = "9023",
    httpPort = "9024",
    error = error,
)
