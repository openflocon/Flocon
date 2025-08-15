package io.github.openflocon.domain.adb.repository

import io.github.openflocon.domain.adb.AdbCommandTargetDomainModel
import io.github.openflocon.domain.common.Either

interface AdbRepository {

    fun getAdbSerial(deviceId: String) : String?
    fun saveAdbSerial(deviceId: String, serial: String)

    fun executeAdbCommand(
        adbPath: String,
        target: AdbCommandTargetDomainModel,
        command: String,
    ): Either<Throwable, String>

    fun executeAdbAskSerialToAllDevices(
        adbPath: String,
        command: String,
        serialVariableName: String,
    ): Either<Throwable, String>

    fun findAdbPath(): String?

}
