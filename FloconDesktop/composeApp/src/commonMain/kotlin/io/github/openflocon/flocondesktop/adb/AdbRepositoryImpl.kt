package io.github.openflocon.flocondesktop.adb

import io.github.openflocon.domain.adb.model.DeviceWithSerialDomainModel
import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.Either
import io.github.openflocon.data.core.adb.datasource.local.AdbLocalDataSource
import io.github.openflocon.flocondesktop.common.askSerialToAllDevices
import io.github.openflocon.flocondesktop.common.localExecuteAdbCommand
import io.github.openflocon.flocondesktop.common.localFindAdbPath
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

// TODO Move
class AdbRepositoryImpl(
    private val adbLocalDataSource : AdbLocalDataSource,
    private val dispatcherProvider: DispatcherProvider,
) : AdbRepository {

    override val devicesWithSerial = adbLocalDataSource.devicesWithSerial
        .flowOn(dispatcherProvider.data)

    override suspend fun getAdbSerial(deviceId: String): String? {
        return withContext(dispatcherProvider.data) {
            adbLocalDataSource.getFromDeviceId(deviceId = deviceId)?.serial
        }
    }

    override suspend fun saveAdbSerial(deviceId: String, serial: String) {
        withContext(dispatcherProvider.data) {
            adbLocalDataSource.add(
                DeviceWithSerialDomainModel(
                    deviceId = deviceId,
                    serial = serial,
                )
            )
        }
    }

    override fun executeAdbCommand(
        adbPath: String,
        deviceSerial: String?,
        command: String,
    ): Either<Throwable, String> {
        return localExecuteAdbCommand(
            adbPath = adbPath,
            command = command,
            deviceSerial = deviceSerial,
        )
    }

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
