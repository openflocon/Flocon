package io.github.openflocon.domain.settings.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.models.settings.ServerPortsDomainModel
import io.github.openflocon.domain.models.settings.ServerPortsOverrides

/**
 * Reads the server ports launch options: `--websocket-port=<n>` / `--http-port=<n>`
 * (or `--websocket-port <n>`), then the FLOCON_WEBSOCKET_PORT / FLOCON_HTTP_PORT env vars.
 * Per port: argument > env var > nothing (the saved settings are used).
 * Unknown arguments are ignored.
 */
object ParseServerPortsLaunchOptions {
    const val WEBSOCKET_PORT_ARG = "--websocket-port"
    const val HTTP_PORT_ARG = "--http-port"
    const val WEBSOCKET_PORT_ENV = "FLOCON_WEBSOCKET_PORT"
    const val HTTP_PORT_ENV = "FLOCON_HTTP_PORT"

    /** On failure, a message ready to be displayed. */
    fun parse(args: List<String>, env: Map<String, String>): Either<String, ServerPortsOverrides> {
        val websocket = resolve(WEBSOCKET_PORT_ARG, WEBSOCKET_PORT_ENV, args, env)
        val http = resolve(HTTP_PORT_ARG, HTTP_PORT_ENV, args, env)
        if (websocket is Failure) return Failure(websocket.value)
        if (http is Failure) return Failure(http.value)
        return Success(
            ServerPortsOverrides(
                websocketPort = (websocket as Success).value,
                httpPort = (http as Success).value,
            ),
        )
    }

    /** Validates the pair that will really be used: the overrides merged with the [saved] ports. */
    fun validateEffective(overrides: ServerPortsOverrides, saved: ServerPortsDomainModel): Either<String, ServerPortsDomainModel> {
        val effective = overrides.applyTo(saved)
        val error = UpdateServerPortsUseCase.validate(effective) ?: return Success(effective)
        val reason = when (error) {
            ServerPortsError.OutOfRange -> "ports must be between 1024 and 65535"
            ServerPortsError.SamePorts -> "the websocket and HTTP ports must be different"
            ServerPortsError.McpPortReserved -> "9042 is reserved for the MCP server"
            ServerPortsError.ReservedDefaultPort -> "9023 can only be the websocket port and 9024 only the HTTP port"
        }
        return Failure(
            "Invalid server ports (websocket=${effective.websocketPort}, http=${effective.httpPort}): $reason.",
        )
    }

    private fun resolve(
        arg: String,
        envName: String,
        args: List<String>,
        env: Map<String, String>,
    ): Either<String, Int?> {
        val fromArg = findArgValue(arg, args)
        if (fromArg != null) return toPort(fromArg, "argument $arg")
        val fromEnv = env[envName]?.takeIf { it.isNotBlank() }
        if (fromEnv != null) return toPort(fromEnv, "environment variable $envName")
        return Success(null)
    }

    // the last occurrence wins; "--x=<n>" and "--x <n>" forms
    private fun findArgValue(name: String, args: List<String>): String? {
        var value: String? = null
        var index = 0
        while (index < args.size) {
            val current = args[index]
            when {
                current.startsWith("$name=") -> value = current.removePrefix("$name=")

                current == name -> {
                    value = args.getOrNull(index + 1) ?: ""
                    index++
                }
            }
            index++
        }
        return value
    }

    private fun toPort(raw: String, source: String): Either<String, Int?> {
        val port = raw.trim().toIntOrNull()
            ?: return Failure("Invalid value \"$raw\" for $source: expected a port number.")
        return Success(port)
    }
}
