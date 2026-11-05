package io.github.openflocon.domain.settings.usecase

import io.github.openflocon.domain.Constant
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.models.settings.ServerPortsDomainModel
import io.github.openflocon.domain.models.settings.ServerPortsOverrides
import io.github.openflocon.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.first

enum class ServerPortsError {
    // not in 1024..65535
    OutOfRange,

    // the websocket and HTTP servers cannot share a port
    SamePorts,

    // the MCP server owns this port
    McpPortReserved,

    // 9023/9024 are the device default adb reverse mappings (ws/http): only usable in their own slot
    ReservedDefaultPort,
}

class UpdateServerPortsUseCase(
    private val settingsRepository: SettingsRepository,
    private val overrides: ServerPortsOverrides,
) {
    suspend operator fun invoke(ports: ServerPortsDomainModel): Either<ServerPortsError, Unit> {
        val error = validate(ports)
        if (error != null) return Failure(error)
        // an overridden port is never persisted, the saved one is kept
        val saved = settingsRepository.savedServerPorts.first()
        settingsRepository.setServerPorts(
            ServerPortsDomainModel(
                websocketPort = if (overrides.websocketPort != null) saved.websocketPort else ports.websocketPort,
                httpPort = if (overrides.httpPort != null) saved.httpPort else ports.httpPort,
            ),
        )
        return Success(Unit)
    }

    companion object {
        val ALLOWED_RANGE = 1024..65535

        fun validate(ports: ServerPortsDomainModel): ServerPortsError? = when {
            ports.websocketPort !in ALLOWED_RANGE || ports.httpPort !in ALLOWED_RANGE -> ServerPortsError.OutOfRange
            ports.websocketPort == ports.httpPort -> ServerPortsError.SamePorts
            ports.websocketPort == Constant.MCP_SERVER_PORT || ports.httpPort == Constant.MCP_SERVER_PORT -> ServerPortsError.McpPortReserved
            ports.websocketPort == Constant.SERVER_HTTP_PORT || ports.httpPort == Constant.SERVER_WEBSOCKET_PORT -> ServerPortsError.ReservedDefaultPort
            else -> null
        }
    }
}
