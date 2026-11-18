package io.github.openflocon.domain.settings.usecase

import io.github.openflocon.domain.Constant
import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.models.settings.ServerPortsDomainModel

class StartAdbForwardUseCase(
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
) {
    // custom mappings (`reverse tcp:<p> tcp:<p>`) applied by this use case, removed when the ports change
    private var appliedCustomPorts: Set<Int> = emptySet()

    /**
     * The SDK defaults (9023 / 9024) are always forwarded to the desktop ports.
     * When a desktop port is not a default one, it is also forwarded on itself,
     * for the SDK apps configured with the same custom port.
     */
    suspend operator fun invoke(ports: ServerPortsDomainModel): Either<Throwable, Unit> {
        val customPorts = customPorts(ports)

        (appliedCustomPorts - customPorts).forEach { old ->
            run("reverse --remove tcp:$old")
                .alsoSuccess { appliedCustomPorts = appliedCustomPorts - old }
        }

        val commands = listOf(
            "reverse tcp:${Constant.SERVER_WEBSOCKET_PORT} tcp:${ports.websocketPort}",
            "reverse tcp:${Constant.SERVER_HTTP_PORT} tcp:${ports.httpPort}",
        ) + customPorts.map { "reverse tcp:$it tcp:$it" }

        for (command in commands) {
            val result = run(command)
            if (result is Failure) return Failure(result.value)
        }
        appliedCustomPorts = appliedCustomPorts + customPorts
        return Success(Unit)
    }

    // a port equal to an SDK default is already covered by the default mappings
    private fun customPorts(ports: ServerPortsDomainModel): Set<Int> = setOf(ports.websocketPort, ports.httpPort) -
        setOf(Constant.SERVER_WEBSOCKET_PORT, Constant.SERVER_HTTP_PORT)

    private suspend fun run(command: String) = executeAdbCommandUseCase(
        command = command,
        target = AdbCommandTargetDomainModel.AllDevices,
    )
}
