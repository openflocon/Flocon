package io.github.openflocon.domain.deeplink.usecase

import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.DeviceWithSerialDomainModel
import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.deeplink.models.DeeplinkDomainModel
import io.github.openflocon.domain.deeplink.models.Deeplinks
import io.github.openflocon.domain.deeplink.repository.DeeplinkRepository
import io.github.openflocon.domain.device.models.AppPackageName
import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.models.HandleDeviceResultDomainModel
import io.github.openflocon.domain.device.models.RegisterDeviceWithAppDomainModel
import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdUseCase
import io.github.openflocon.domain.iossimulator.repository.IosSimulatorRepository
import io.github.openflocon.domain.models.settings.NetworkSettings
import io.github.openflocon.domain.models.settings.AppScaleSetting
import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.domain.settings.repository.AdbForwardStatus
import io.github.openflocon.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ExecuteDeeplinkUseCaseTest {

    private val deviceId = "device-id"
    private val packageName = "com.example.app"

    @Test
    fun `android device opens the deeplink with adb`() = runBlocking {
        val env = Env(platform = "android")

        val result = env.useCase(deeplink = "myapp://home", deeplinkId = -1L, saveIntoHistory = false)

        assertIs<Success<String>>(result)
        assertEquals(
            listOf("shell am start -W -a android.intent.action.VIEW -d \"myapp://home\" $packageName"),
            env.adbRepository.commands,
        )
        assertTrue(env.iosSimulatorRepository.openedUrls.isEmpty())
    }

    @Test
    fun `ios device opens the deeplink in the booted simulator`() = runBlocking {
        val env = Env(platform = "ios")

        val result = env.useCase(deeplink = "myapp://home?id=1&tab=2", deeplinkId = -1L, saveIntoHistory = false)

        assertIs<Success<String>>(result)
        assertEquals(listOf("myapp://home?id=1&tab=2"), env.iosSimulatorRepository.openedUrls)
        assertTrue(env.adbRepository.commands.isEmpty())
    }

    @Test
    fun `ios deeplink is saved into history on success`() = runBlocking {
        val env = Env(platform = "ios")

        env.useCase(deeplink = "myapp://free", deeplinkId = -1L, saveIntoHistory = true)

        assertEquals(listOf("myapp://free"), env.deeplinkRepository.history.map { it.link })
    }

    private inner class Env(platform: String) {
        val adbRepository = FakeAdbRepository()
        val iosSimulatorRepository = FakeIosSimulatorRepository()
        val deeplinkRepository = FakeDeeplinkRepository()
        private val devicesRepository = FakeDevicesRepository(
            device = DeviceDomainModel(deviceId = deviceId, deviceName = "device", platform = platform),
            app = DeviceAppDomainModel(
                name = "app",
                packageName = packageName,
                iconEncoded = null,
                lastAppInstance = 0L,
                floconVersionOnDevice = "1.0.0",
            ),
        )
        private val getCurrentDeviceIdAndPackageNameUseCase = GetCurrentDeviceIdAndPackageNameUseCase(
            getCurrentDeviceIdUseCase = GetCurrentDeviceIdUseCase(devicesRepository),
            devicesRepository = devicesRepository,
        )

        val useCase = ExecuteDeeplinkUseCase(
            executeAdbCommandUseCase = ExecuteAdbCommandUseCase(
                adbRepository = adbRepository,
                settingsRepository = FakeSettingsRepository(),
            ),
            getCurrentDeviceIdAndPackageNameUseCase = getCurrentDeviceIdAndPackageNameUseCase,
            addToDeeplinkHistoryUseCase = AddToDeeplinkHistoryUseCase(
                deeplinkRepository = deeplinkRepository,
                getCurrentDeviceIdAndPackageNameUseCase = getCurrentDeviceIdAndPackageNameUseCase,
            ),
            deeplinkRepository = deeplinkRepository,
            devicesRepository = devicesRepository,
            iosSimulatorRepository = iosSimulatorRepository,
        )
    }

    private class FakeIosSimulatorRepository : IosSimulatorRepository {
        val openedUrls = mutableListOf<String>()
        override suspend fun openUrl(url: String): Either<Throwable, String> {
            openedUrls += url
            return Success("")
        }

        override suspend fun takeScreenshot(filePath: String): Either<Throwable, String> = Success(filePath)

        override suspend fun startRecording(filePath: String): Either<Throwable, Unit> = Success(Unit)

        override suspend fun stopRecording(filePath: String): Either<Throwable, String> = Success(filePath)
    }

    private class FakeAdbRepository : AdbRepository {
        val commands = mutableListOf<String>()
        override suspend fun getAdbSerial(deviceId: String): String? = null
        override suspend fun saveAdbSerial(deviceId: String, serial: String) {}
        override val devicesWithSerial: Flow<Collection<DeviceWithSerialDomainModel>> = emptyFlow()
        override fun executeAdbCommand(adbPath: String, deviceSerial: String?, command: String): Either<Throwable, String> {
            commands += command
            return Success("")
        }
        override fun executeAdbAskSerialToAllDevices(
            adbPath: String,
            command: String,
            serialVariableName: String,
        ): Either<Throwable, String> = Success("")
        override fun findAdbPath(): String? = "adb"
    }

    private class FakeDeeplinkRepository : DeeplinkRepository {
        val history = mutableListOf<DeeplinkDomainModel>()
        override fun observe(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<Deeplinks> = emptyFlow()
        override suspend fun getDeeplinkById(
            deeplinkId: Long,
            deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        ): DeeplinkDomainModel? = null
        override fun observeHistory(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DeeplinkDomainModel>> = emptyFlow()
        override suspend fun addToHistory(item: DeeplinkDomainModel, deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
            history += item
        }
        override suspend fun removeFromHistory(deeplinkId: Long, deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {}
    }

    private class FakeDevicesRepository(
        private val device: DeviceDomainModel,
        private val app: DeviceAppDomainModel,
    ) : DevicesRepository {
        override val devices: Flow<List<DeviceDomainModel>> = MutableStateFlow(listOf(device))
        override val currentDeviceId: Flow<DeviceId?> = MutableStateFlow(device.deviceId)
        override val activeDevices: Flow<Set<DeviceIdAndPackageNameDomainModel>> = emptyFlow()
        override suspend fun getCurrentDeviceId(): DeviceId = device.deviceId
        override suspend fun register(registerDeviceWithApp: RegisterDeviceWithAppDomainModel): HandleDeviceResultDomainModel = error("not used")
        override suspend fun getCurrentDevice(): DeviceDomainModel = device
        override suspend fun selectDevice(deviceId: DeviceId) {}
        override fun observeDeviceApps(deviceId: DeviceId): Flow<List<DeviceAppDomainModel>> = MutableStateFlow(listOf(app))
        override fun observeDeviceSelectedApp(deviceId: DeviceId): Flow<DeviceAppDomainModel?> = MutableStateFlow(app)
        override suspend fun getDeviceSelectedApp(deviceId: DeviceId): DeviceAppDomainModel = app
        override suspend fun getDeviceAppByPackage(deviceId: DeviceId, appPackageName: String): DeviceAppDomainModel = app
        override suspend fun selectApp(deviceId: DeviceId, app: DeviceAppDomainModel) {}
        override suspend fun saveAppIcon(deviceId: DeviceId, appPackageName: String, iconEncoded: String) {}
        override suspend fun hasAppIcon(deviceId: DeviceId, appPackageName: String): Boolean = false
        override suspend fun askForDeviceAppIcon(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {}
        override fun observeDeviceSdkVersion(deviceId: DeviceId, appPackageName: String): Flow<String?> = emptyFlow()
        override suspend fun deleteDevice(deviceId: DeviceId) {}
        override suspend fun deleteApplication(deviceId: DeviceId, packageName: AppPackageName) {}
        override suspend fun restartApp(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {}
        override suspend fun clear() {}
        override fun observeCurrentDevice(): Flow<DeviceDomainModel?> = MutableStateFlow(device)
    }

    private class FakeSettingsRepository : SettingsRepository {
        override var networkSettings: NetworkSettings = NetworkSettings(
            pinnedDetails = false,
            displayOldSessions = false,
            autoScroll = false,
            invertList = false,
        )
        override val networkSettingsFlow: Flow<NetworkSettings> = emptyFlow()
        override fun getAdbPath(): String = "adb"
        override suspend fun setAdbPath(path: String) {}
        override suspend fun setAppScale(value: AppScaleSetting) {}
        override suspend fun setTheme(value: ThemeSetting) {}
        override fun getFloconDocumentsPath(): String? = null
        override suspend fun setFloconDocumentsPath(path: String) {}
        override val floconDocumentsPath: Flow<String?> = emptyFlow()
        override fun getDismissedDesktopVersion(): String? = null
        override suspend fun setDismissedDesktopVersion(version: String) {}
        override val dismissedClientVersionFlow: Flow<String?> = emptyFlow()
        override fun getDismissedClientVersion(): String? = null
        override suspend fun setDismissedClientVersion(version: String) {}
        override fun getLastSeenReleaseNotesVersion(): String? = null
        override suspend fun setLastSeenReleaseNotesVersion(version: String) {}
        override val adbPath: Flow<String?> = emptyFlow()
        override val appScale: StateFlow<AppScaleSetting> = MutableStateFlow(AppScaleSetting.DEFAULT)
        override val theme: StateFlow<ThemeSetting> = MutableStateFlow(ThemeSetting.DEFAULT)
        override val adbForwardStatus: StateFlow<AdbForwardStatus> = MutableStateFlow(AdbForwardStatus.UNKNOWN)
        override fun setAdbForwardStatus(status: AdbForwardStatus) {}
    }
}
