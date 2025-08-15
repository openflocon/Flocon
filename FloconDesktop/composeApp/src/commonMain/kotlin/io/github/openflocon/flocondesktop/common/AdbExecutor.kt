package io.github.openflocon.flocondesktop.common

import io.github.openflocon.domain.common.Either

expect fun localFindAdbPath(): String?

expect fun localExecuteAdbCommand(
    adbPath: String,
    command: String,
    deviceSerial: String?,
): Either<Throwable, String>

expect fun askSerialToAllDevices(adbPath: String, command: String, serialVariableName: String): Either<Throwable, String>
