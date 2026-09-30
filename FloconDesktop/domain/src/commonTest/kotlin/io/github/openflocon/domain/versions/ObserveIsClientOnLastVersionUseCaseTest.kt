package io.github.openflocon.domain.versions

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.device.models.AppPackageName
import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.models.HandleDeviceResultDomainModel
import io.github.openflocon.domain.device.models.RegisterDeviceWithAppDomainModel
import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceFloconSdkVersionNameUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdUseCase
import io.github.openflocon.domain.models.settings.NetworkSettings
import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.domain.settings.repository.AdbForwardStatus
import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.domain.versions.model.IsLastVersionDomainModel
import io.github.openflocon.domain.versions.repository.VersionsCheckerRepository
import io.github.openflocon.domain.versions.usecase.DismissClientVersionUseCase
import io.github.openflocon.domain.versions.usecase.ObserveIsClientOnLastVersionUseCase
import io.github.openflocon.domain.versions.usecase.ObserveLastAvailableFloconVersionUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ObserveIsClientOnLastVersionUseCaseTest {

    private class FakeVersionsCheckerRepository(
        initialVersion: String? = null
    ) : VersionsCheckerRepository {
        val versionFlow = MutableStateFlow(initialVersion)
        override val lastVersion: Flow<String?> = versionFlow.asStateFlow()
        override suspend fun checkIsLastVersion(): Either<Throwable, String> = throw NotImplementedError()
    }

    private class FakeSettingsRepository(
        initialDismissedClientVersion: String? = null
    ) : SettingsRepository {
        val dismissedClientFlow = MutableStateFlow(initialDismissedClientVersion)
        override val dismissedClientVersionFlow: Flow<String?> = dismissedClientFlow.asStateFlow()
        override fun getDismissedClientVersion(): String? = dismissedClientFlow.value
        override suspend fun setDismissedClientVersion(version: String) {
            dismissedClientFlow.value = version
        }
        override var networkSettings: NetworkSettings = NetworkSettings(
            pinnedDetails = false,
            displayOldSessions = false,
            autoScroll = false,
            invertList = false,
        )
        override val networkSettingsFlow: Flow<NetworkSettings> = emptyFlow()
        override fun getAdbPath(): String? = null
        override suspend fun setAdbPath(path: String) {}
        override suspend fun setFontSizeMultiplier(value: Float) {}
        override suspend fun setTheme(value: ThemeSetting) {}
        override fun getDismissedDesktopVersion(): String? = null
        override suspend fun setDismissedDesktopVersion(version: String) {}
        override val adbPath: Flow<String?> = emptyFlow()
        override val fontSizeMultiplier: StateFlow<Float> = MutableStateFlow(1f)
        override val theme: StateFlow<ThemeSetting> = MutableStateFlow(ThemeSetting.DEFAULT)
        override val adbForwardStatus: StateFlow<AdbForwardStatus> = MutableStateFlow(AdbForwardStatus.UNKNOWN)
        override fun setAdbForwardStatus(status: AdbForwardStatus) {}
    }

    private class FakeDevicesRepository(
        var currentDeviceIdValue: String? = "device1",
        var sdkVersionValue: String? = "1.0.0",
    ) : DevicesRepository {
        override val devices: Flow<List<DeviceDomainModel>> = emptyFlow()
        override val currentDeviceId: Flow<DeviceId?> = flowOf(currentDeviceIdValue)
        override val activeDevices: Flow<Set<DeviceIdAndPackageNameDomainModel>> = emptyFlow()

        override suspend fun getCurrentDeviceId(): DeviceId? = currentDeviceIdValue
        override suspend fun register(registerDeviceWithApp: RegisterDeviceWithAppDomainModel): HandleDeviceResultDomainModel = throw NotImplementedError()
        override suspend fun getCurrentDevice(): DeviceDomainModel? = null
        override suspend fun selectDevice(deviceId: DeviceId) {}

        override fun observeDeviceApps(deviceId: DeviceId): Flow<List<DeviceAppDomainModel>> = emptyFlow()
        override fun observeDeviceSelectedApp(deviceId: DeviceId): Flow<DeviceAppDomainModel?> = flowOf(
            DeviceAppDomainModel(
                name = "TestApp",
                packageName = "com.test.app",
                iconEncoded = null,
                lastAppInstance = 1,
                floconVersionOnDevice = sdkVersionValue ?: "",
            )
        )
        override suspend fun getDeviceSelectedApp(deviceId: DeviceId): DeviceAppDomainModel? = null
        override suspend fun getDeviceAppByPackage(deviceId: DeviceId, appPackageName: String): DeviceAppDomainModel? = null
        override suspend fun selectApp(deviceId: DeviceId, app: DeviceAppDomainModel) {}

        override suspend fun saveAppIcon(deviceId: DeviceId, appPackageName: String, iconEncoded: String) {}
        override suspend fun hasAppIcon(deviceId: DeviceId, appPackageName: String): Boolean = false
        override suspend fun askForDeviceAppIcon(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {}

        override fun observeDeviceSdkVersion(deviceId: DeviceId, appPackageName: String): Flow<String?> = flowOf(sdkVersionValue)

        override suspend fun deleteDevice(deviceId: DeviceId) {}
        override suspend fun deleteApplication(deviceId: DeviceId, packageName: AppPackageName) {}
        override suspend fun restartApp(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {}
        override suspend fun clear() {}
        override fun observeCurrentDevice(): Flow<DeviceDomainModel?> = emptyFlow()
    }

    private fun createObserveSdkUseCase(devicesRepo: DevicesRepository): ObserveCurrentDeviceFloconSdkVersionNameUseCase {
        val observeCurrentDeviceIdUseCase = ObserveCurrentDeviceIdUseCase(devicesRepo)
        val observeCurrentDeviceIdAndPackageNameUseCase = ObserveCurrentDeviceIdAndPackageNameUseCase(
            observeCurrentDeviceIdUseCase = observeCurrentDeviceIdUseCase,
            devicesRepository = devicesRepo,
        )
        return ObserveCurrentDeviceFloconSdkVersionNameUseCase(
            observeCurrentDeviceIdAndPackageNameUseCase = observeCurrentDeviceIdAndPackageNameUseCase,
            devicesRepository = devicesRepo,
        )
    }

    @Test
    fun `when remote client version is newer and no dismissed version, emits NewVersionAvailable`() {
        runBlocking {
            val versionsRepo = FakeVersionsCheckerRepository(initialVersion = "1.1.0")
            val settingsRepo = FakeSettingsRepository(initialDismissedClientVersion = null)
            val observeRemoteUseCase = ObserveLastAvailableFloconVersionUseCase(versionsRepo)
            val devicesRepo = FakeDevicesRepository(sdkVersionValue = "1.0.0")
            val observeSdkUseCase = createObserveSdkUseCase(devicesRepo)

            val useCase = ObserveIsClientOnLastVersionUseCase(
                observeLastAvailableFloconVersionUseCase = observeRemoteUseCase,
                observeCurrentDeviceFloconSdkVersionNameUseCase = observeSdkUseCase,
                settingsRepository = settingsRepo,
            )

            val result = useCase().first()
            assertIs<IsLastVersionDomainModel.NewVersionAvailable>(result)
            assertEquals("1.1.0", result.name)
            assertEquals("1.0.0", result.oldVersion)
        }
    }

    @Test
    fun `when remote client version is newer and dismissed, emits RunningLastVersion`() {
        runBlocking {
            val versionsRepo = FakeVersionsCheckerRepository(initialVersion = "1.1.0")
            val settingsRepo = FakeSettingsRepository(initialDismissedClientVersion = "1.1.0")
            val observeRemoteUseCase = ObserveLastAvailableFloconVersionUseCase(versionsRepo)
            val devicesRepo = FakeDevicesRepository(sdkVersionValue = "1.0.0")
            val observeSdkUseCase = createObserveSdkUseCase(devicesRepo)

            val useCase = ObserveIsClientOnLastVersionUseCase(
                observeLastAvailableFloconVersionUseCase = observeRemoteUseCase,
                observeCurrentDeviceFloconSdkVersionNameUseCase = observeSdkUseCase,
                settingsRepository = settingsRepo,
            )

            val result = useCase().first()
            assertIs<IsLastVersionDomainModel.RunningLastVersion>(result)
        }
    }

    @Test
    fun `test complete client dismissal and newer version lifecycle`() {
        runBlocking {
            val versionsRepo = FakeVersionsCheckerRepository(initialVersion = "1.1.0")
            val settingsRepo = FakeSettingsRepository(initialDismissedClientVersion = null)
            val observeRemoteUseCase = ObserveLastAvailableFloconVersionUseCase(versionsRepo)
            val devicesRepo = FakeDevicesRepository(sdkVersionValue = "1.0.0")
            val observeSdkUseCase = createObserveSdkUseCase(devicesRepo)

            val useCase = ObserveIsClientOnLastVersionUseCase(
                observeLastAvailableFloconVersionUseCase = observeRemoteUseCase,
                observeCurrentDeviceFloconSdkVersionNameUseCase = observeSdkUseCase,
                settingsRepository = settingsRepo,
            )
            val dismissUseCase = DismissClientVersionUseCase(settingsRepo)

            // 1. Initial check: New version available
            val result1 = useCase().first()
            assertIs<IsLastVersionDomainModel.NewVersionAvailable>(result1)
            assertEquals("1.1.0", result1.name)

            // 2. Dismiss 1.1.0
            dismissUseCase("1.1.0")

            // 3. Check again: Suppressed
            val result2 = useCase().first()
            assertIs<IsLastVersionDomainModel.RunningLastVersion>(result2)

            // 4. Remote version updated to 1.2.0: Emits NewVersionAvailable for 1.2.0
            versionsRepo.versionFlow.value = "1.2.0"
            val result3 = useCase().first()
            assertIs<IsLastVersionDomainModel.NewVersionAvailable>(result3)
            assertEquals("1.2.0", result3.name)
        }
    }
}
