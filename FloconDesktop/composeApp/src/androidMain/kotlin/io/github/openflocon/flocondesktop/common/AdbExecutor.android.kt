package io.github.openflocon.flocondesktop.common

import io.github.openflocon.domain.common.Either

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
