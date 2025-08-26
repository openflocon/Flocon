package io.github.openflocon.flocondesktop.common

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.models.ProcessId

actual fun localExecuteAdbCommand(
    adbPath: String,
    command: String,
    deviceSerial: String?,
): Either<Throwable, String> {
    TODO("Not yet implemented")
}

actual fun localFindAdbPath(): String? {
    TODO("Not yet implemented")
}

actual fun askSerialToAllDevices(
    adbPath: String,
    command: String,
    serialVariableName: String,
): Either<Throwable, String> {
    TODO("Not yet implemented")
}

actual fun startProcess(
    adbPath: String,
    deviceSerial: String?,
    command: String
): Either<Throwable, ProcessId> {
    TODO("Not yet implemented")
}

actual fun stopProcess(processId: ProcessId) {
}
