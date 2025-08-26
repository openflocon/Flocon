package io.github.openflocon.flocondesktop.common

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.models.ProcessId

expect fun localFindAdbPath(): String?

expect fun localExecuteAdbCommand(
    adbPath: String,
    command: String,
    deviceSerial: String?,
): Either<Throwable, String>

expect fun askSerialToAllDevices(adbPath: String, command: String, serialVariableName: String): Either<Throwable, String>

expect fun startProcess(
    adbPath: String,
    deviceSerial: String?,
    command: String
): Either<Throwable, ProcessId>

expect fun stopProcess(processId: ProcessId)
