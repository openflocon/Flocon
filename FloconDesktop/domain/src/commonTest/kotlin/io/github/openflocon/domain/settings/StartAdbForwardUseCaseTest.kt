package io.github.openflocon.domain.settings

import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.DeviceWithSerialDomainModel
import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.device.models.AppPackageName
import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.models.HandleDeviceResultDomainModel
import io.github.openflocon.domain.device.models.RegisterDeviceWithAppDomainModel
import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.models.settings.ServerPortsDomainModel
import io.github.openflocon.domain.settings.usecase.StartAdbForwardUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class StartAdbForwardUseCaseTest {

    private val adbRepository = FakeAdbRepository()
    private val useCase = StartAdbForwardUseCase(
        ExecuteAdbCommandUseCase(
            adbRepository = adbRepository,
            settingsRepository = FakeSettingsRepository(),
            devicesRepository = FakeDevicesRepository(),
        ),
    )

    @Test
    fun `default ports forward the defaults onto themselves`() = runBlocking {
        val result = useCase(ServerPortsDomainModel())

        assertIs<Success<Unit>>(result)
        assertEquals(
            listOf("reverse tcp:9023 tcp:9023", "reverse tcp:9024 tcp:9024"),
            adbRepository.commands,
        )
    }

    @Test
    fun `custom ports forward the device defaults to the desktop ports and the custom ports onto themselves`() = runBlocking {
        useCase(ServerPortsDomainModel(websocketPort = 9100, httpPort = 9101))

        assertEquals(
            listOf(
                "reverse tcp:9023 tcp:9100",
                "reverse tcp:9024 tcp:9101",
                "reverse tcp:9100 tcp:9100",
                "reverse tcp:9101 tcp:9101",
            ),
            adbRepository.commands,
        )
    }

    @Test
    fun `only one custom port also maps that port`() = runBlocking {
        useCase(ServerPortsDomainModel(websocketPort = 9100, httpPort = 9024))

        assertEquals(
            listOf("reverse tcp:9023 tcp:9100", "reverse tcp:9024 tcp:9024", "reverse tcp:9100 tcp:9100"),
            adbRepository.commands,
        )
    }

    @Test
    fun `changing the ports removes only the custom mappings applied before`() = runBlocking {
        useCase(ServerPortsDomainModel(websocketPort = 9100, httpPort = 9101))
        adbRepository.commands.clear()

        useCase(ServerPortsDomainModel(websocketPort = 9100, httpPort = 9200))

        assertEquals(
            listOf(
                "reverse --remove tcp:9101",
                "reverse tcp:9023 tcp:9100",
                "reverse tcp:9024 tcp:9200",
                "reverse tcp:9100 tcp:9100",
                "reverse tcp:9200 tcp:9200",
            ),
            adbRepository.commands,
        )
    }

    @Test
    fun `going back to the defaults removes the custom mappings and never the default ones`() = runBlocking {
        useCase(ServerPortsDomainModel(websocketPort = 9100, httpPort = 9101))
        adbRepository.commands.clear()

        useCase(ServerPortsDomainModel())

        assertEquals(
            listOf(
                "reverse --remove tcp:9100",
                "reverse --remove tcp:9101",
                "reverse tcp:9023 tcp:9023",
                "reverse tcp:9024 tcp:9024",
            ),
            // the removal order follows a set: compare it without relying on it
            adbRepository.commands.take(2).sorted() + adbRepository.commands.drop(2),
        )
    }

    @Test
    fun `a failing command is returned and nothing custom is remembered`() = runBlocking {
        adbRepository.failingCommands += "reverse tcp:9023 tcp:9100"

        val result = useCase(ServerPortsDomainModel(websocketPort = 9100, httpPort = 9101))

        assertIs<Failure<Throwable>>(result)
        assertEquals(listOf("reverse tcp:9023 tcp:9100"), adbRepository.commands)

        // never applied: nothing to remove when going back to the defaults
        adbRepository.commands.clear()
        adbRepository.failingCommands.clear()
        useCase(ServerPortsDomainModel())
        assertEquals(listOf("reverse tcp:9023 tcp:9023", "reverse tcp:9024 tcp:9024"), adbRepository.commands)
    }

    private class FakeAdbRepository : AdbRepository {
        val commands = mutableListOf<String>()
        val failingCommands = mutableSetOf<String>()
        override suspend fun getAdbSerial(deviceId: String): String? = null
        override suspend fun saveAdbSerial(deviceId: String, serial: String) {}
        override val devicesWithSerial: Flow<Collection<DeviceWithSerialDomainModel>> = emptyFlow()
        override fun executeAdbCommand(adbPath: String, deviceSerial: String?, command: String): Either<Throwable, String> {
            commands += command
            return if (command in failingCommands) Failure(Throwable("failed")) else Success("")
        }
        override fun executeAdbAskSerialToAllDevices(
            adbPath: String,
            command: String,
            serialVariableName: String,
        ): Either<Throwable, String> = Success("")
        override fun findAdbPath(): String? = "adb"
    }

    private class FakeDevicesRepository : DevicesRepository {
        override val devices: Flow<List<DeviceDomainModel>> = emptyFlow()
        override val currentDeviceId: Flow<DeviceId?> = emptyFlow()
        override val activeDevices: Flow<Set<DeviceIdAndPackageNameDomainModel>> = emptyFlow()
        override suspend fun getCurrentDeviceId(): DeviceId = error("not used")
        override suspend fun register(registerDeviceWithApp: RegisterDeviceWithAppDomainModel): HandleDeviceResultDomainModel = error("not used")
        override suspend fun getCurrentDevice(): DeviceDomainModel = error("not used")
        override suspend fun selectDevice(deviceId: DeviceId) {}
        override fun observeDeviceApps(deviceId: DeviceId): Flow<List<DeviceAppDomainModel>> = emptyFlow()
        override fun observeDeviceSelectedApp(deviceId: DeviceId): Flow<DeviceAppDomainModel?> = emptyFlow()
        override suspend fun getDeviceSelectedApp(deviceId: DeviceId): DeviceAppDomainModel = error("not used")
        override suspend fun getDeviceAppByPackage(deviceId: DeviceId, appPackageName: String): DeviceAppDomainModel = error("not used")
        override suspend fun selectApp(deviceId: DeviceId, app: DeviceAppDomainModel) {}
        override suspend fun saveAppIcon(deviceId: DeviceId, appPackageName: String, iconEncoded: String) {}
        override suspend fun hasAppIcon(deviceId: DeviceId, appPackageName: String): Boolean = false
        override suspend fun askForDeviceAppIcon(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {}
        override fun observeDeviceSdkVersion(deviceId: DeviceId, appPackageName: String): Flow<String?> = emptyFlow()
        override fun observeDeviceSdk(deviceId: DeviceId, appPackageName: String): Flow<String?> = emptyFlow()
        override suspend fun deleteDevice(deviceId: DeviceId) {}
        override suspend fun deleteApplication(deviceId: DeviceId, packageName: AppPackageName) {}
        override suspend fun clear() {}
        override fun observeCurrentDevice(): Flow<DeviceDomainModel?> = emptyFlow()
    }
}
