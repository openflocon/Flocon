package io.github.openflocon.domain.adb.repository

import io.github.openflocon.domain.adb.model.DeviceWithSerialDomainModel
import io.github.openflocon.domain.common.Either
import kotlinx.coroutines.flow.Flow

interface AdbRepository {

    suspend fun getAdbSerial(deviceId: String): String?

    suspend fun saveAdbSerial(deviceId: String, serial: String)

    val devicesWithSerial: Flow<Collection<DeviceWithSerialDomainModel>>

    fun executeAdbCommand(
        adbPath: String,
        deviceSerial: String?,
        command: String,
    ): Either<Throwable, String>

    fun executeAdbAskSerialToAllDevices(
        adbPath: String,
        command: String,
        serialVariableName: String,
    ): Either<Throwable, String>

    fun findAdbPath(): String?
}
