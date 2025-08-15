package io.github.openflocon.flocondesktop.adb

import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.common.Either
import io.github.openflocon.flocondesktop.common.askSerialToAllDevices
import io.github.openflocon.flocondesktop.common.localExecuteAdbCommand
import io.github.openflocon.flocondesktop.common.localFindAdbPath
import io.ktor.util.collections.ConcurrentMap

// TODO Move
class AdbRepositoryImpl : AdbRepository {

    val adbSerials = ConcurrentMap<String, String>()

    override fun getAdbSerial(deviceId: String): String? {
        return adbSerials[deviceId]
    }

    override fun saveAdbSerial(deviceId: String, serial: String) {
        adbSerials[deviceId] = serial
    }

    override fun executeAdbCommand(
        adbPath: String,
        command: String,
    ): Either<Throwable, String> = localExecuteAdbCommand(adbPath = adbPath, command = command)

    override fun executeAdbAskSerialToAllDevices(
        adbPath: String,
        command: String,
        serialVariableName: String,
    ): Either<Throwable, String> = askSerialToAllDevices(
        adbPath = adbPath,
        command = command,
        serialVariableName = serialVariableName
    )

    override fun findAdbPath(): String? = localFindAdbPath()
}
