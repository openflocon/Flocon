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
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceFloconSdkUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceFloconSdkVersionNameUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdUseCase
import io.github.openflocon.domain.models.settings.AppScaleSetting
import io.github.openflocon.domain.models.settings.NetworkSettings
import io.github.openflocon.domain.models.settings.ServerPortsDomainModel
import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.domain.settings.repository.AdbForwardStatus
import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.domain.versions.model.FloconSdk
import io.github.openflocon.domain.versions.model.IsLastVersionDomainModel
import io.github.openflocon.domain.versions.repository.VersionsCheckerRepository
import io.github.openflocon.domain.versions.usecase.DismissClientVersionUseCase
import io.github.openflocon.domain.versions.usecase.ObserveIsClientOnLastVersionUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class ObserveIsClientOnLastVersionUseCaseTest {

    private class FakeVersionsCheckerRepository(
        initialVersion: String? = null,
        sdkVersions: Map<FloconSdk, String> = emptyMap(),
    ) : VersionsCheckerRepository {
        val versionFlow = MutableStateFlow(initialVersion)
        val sdkVersionsFlow = MutableStateFlow(sdkVersions)
        val checkedSdks = mutableListOf<FloconSdk>()
        override val lastDesktopVersion: Flow<String?> = versionFlow.asStateFlow()
        override suspend fun checkIsLastVersion(): Either<Throwable, String> = throw NotImplementedError()
        override fun observeLastClientSdkVersion(sdk: FloconSdk): Flow<String?> = sdkVersionsFlow.map { it[sdk] }
        override suspend fun checkLastClientSdkVersion(sdk: FloconSdk) {
            checkedSdks += sdk
        }
        override suspend fun getReleaseNotes(): Either<Throwable, List<io.github.openflocon.domain.versions.model.ReleaseNoteDomainModel>> = throw NotImplementedError()
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
        override suspend fun setAppScale(value: AppScaleSetting) {}
        override suspend fun setTheme(value: ThemeSetting) {}
        override fun getFloconDocumentsPath(): String? = null
        override suspend fun setFloconDocumentsPath(path: String) {}
        override val floconDocumentsPath: Flow<String?> = emptyFlow()
        override fun getDismissedDesktopVersion(): String? = null
        override suspend fun setDismissedDesktopVersion(version: String) {}
        override fun getLastSeenReleaseNotesVersion(): String? = null
        override suspend fun setLastSeenReleaseNotesVersion(version: String) {}
        override val adbPath: Flow<String?> = emptyFlow()
        override val savedServerPorts: Flow<ServerPortsDomainModel> = emptyFlow()
        override suspend fun setServerPorts(ports: ServerPortsDomainModel) {}
        override val appScale: StateFlow<AppScaleSetting> = MutableStateFlow(AppScaleSetting.DEFAULT)
        override val theme: StateFlow<ThemeSetting> = MutableStateFlow(ThemeSetting.DEFAULT)
        override val adbForwardStatus: StateFlow<AdbForwardStatus> = MutableStateFlow(AdbForwardStatus.UNKNOWN)
        override fun setAdbForwardStatus(status: AdbForwardStatus) {}
    }

    private class FakeDevicesRepository(
        var currentDeviceIdValue: String? = "device1",
        var sdkVersionValue: String? = "1.0.0",
        var sdkValue: String? = "kotlin",
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

        override fun observeDeviceSdk(deviceId: DeviceId, appPackageName: String): Flow<String?> = flowOf(sdkValue)

        override suspend fun deleteDevice(deviceId: DeviceId) {}
        override suspend fun deleteApplication(deviceId: DeviceId, packageName: AppPackageName) {}
        override suspend fun clear() {}
        override fun observeCurrentDevice(): Flow<DeviceDomainModel?> = emptyFlow()
    }

    private fun createUseCase(
        versionsRepo: VersionsCheckerRepository,
        settingsRepo: SettingsRepository,
        devicesRepo: DevicesRepository,
    ): ObserveIsClientOnLastVersionUseCase {
        val observeCurrentDeviceIdAndPackageNameUseCase = ObserveCurrentDeviceIdAndPackageNameUseCase(
            observeCurrentDeviceIdUseCase = ObserveCurrentDeviceIdUseCase(devicesRepo),
            devicesRepository = devicesRepo,
        )
        return ObserveIsClientOnLastVersionUseCase(
            observeCurrentDeviceFloconSdkUseCase = ObserveCurrentDeviceFloconSdkUseCase(
                observeCurrentDeviceIdAndPackageNameUseCase = observeCurrentDeviceIdAndPackageNameUseCase,
                devicesRepository = devicesRepo,
            ),
            observeCurrentDeviceFloconSdkVersionNameUseCase = ObserveCurrentDeviceFloconSdkVersionNameUseCase(
                observeCurrentDeviceIdAndPackageNameUseCase = observeCurrentDeviceIdAndPackageNameUseCase,
                devicesRepository = devicesRepo,
            ),
            versionsCheckerRepository = versionsRepo,
            settingsRepository = settingsRepo,
        )
    }

    @Test
    fun `when remote client version is newer and no dismissed version, emits NewVersionAvailable`() {
        runBlocking {
            val versionsRepo = FakeVersionsCheckerRepository(sdkVersions = mapOf(FloconSdk.KOTLIN to "1.1.0"))
            val settingsRepo = FakeSettingsRepository(initialDismissedClientVersion = null)
            val devicesRepo = FakeDevicesRepository(sdkVersionValue = "1.0.0")

            val useCase = createUseCase(versionsRepo, settingsRepo, devicesRepo)

            val result = useCase().first()
            assertIs<IsLastVersionDomainModel.NewVersionAvailable>(result)
            assertEquals("1.1.0", result.name)
            assertEquals("1.0.0", result.oldVersion)
        }
    }

    @Test
    fun `when remote client version is newer and dismissed, emits RunningLastVersion`() {
        runBlocking {
            val versionsRepo = FakeVersionsCheckerRepository(sdkVersions = mapOf(FloconSdk.KOTLIN to "1.1.0"))
            val settingsRepo = FakeSettingsRepository(initialDismissedClientVersion = "1.1.0")
            val devicesRepo = FakeDevicesRepository(sdkVersionValue = "1.0.0")

            val useCase = createUseCase(versionsRepo, settingsRepo, devicesRepo)

            val result = useCase().first()
            assertIs<IsLastVersionDomainModel.RunningLastVersion>(result)
        }
    }

    @Test
    fun `test complete client dismissal and newer version lifecycle`() {
        runBlocking {
            val versionsRepo = FakeVersionsCheckerRepository(sdkVersions = mapOf(FloconSdk.KOTLIN to "1.1.0"))
            val settingsRepo = FakeSettingsRepository(initialDismissedClientVersion = null)
            val devicesRepo = FakeDevicesRepository(sdkVersionValue = "1.0.0")

            val useCase = createUseCase(versionsRepo, settingsRepo, devicesRepo)
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
            versionsRepo.sdkVersionsFlow.value = mapOf(FloconSdk.KOTLIN to "1.2.0")
            val result3 = useCase().first()
            assertIs<IsLastVersionDomainModel.NewVersionAvailable>(result3)
            assertEquals("1.2.0", result3.name)
        }
    }

    @Test
    fun `a Swift app is compared with the Swift release, not the Kotlin one`() {
        runBlocking {
            val versionsRepo = FakeVersionsCheckerRepository(
                sdkVersions = mapOf(FloconSdk.KOTLIN to "2.1.0", FloconSdk.SWIFT to "2.0.0", FloconSdk.FLUTTER to "2.0.1"),
            )
            val devicesRepo = FakeDevicesRepository(sdkVersionValue = "2.0.0", sdkValue = "swift")
            val useCase = createUseCase(versionsRepo, FakeSettingsRepository(), devicesRepo)

            assertIs<IsLastVersionDomainModel.RunningLastVersion>(useCase().first())
            assertEquals(listOf(FloconSdk.SWIFT), versionsRepo.checkedSdks)
        }
    }

    @Test
    fun `a Flutter app with an older version is offered the Flutter release`() {
        runBlocking {
            val versionsRepo = FakeVersionsCheckerRepository(
                sdkVersions = mapOf(FloconSdk.KOTLIN to "2.1.0", FloconSdk.FLUTTER to "2.0.1"),
            )
            val devicesRepo = FakeDevicesRepository(sdkVersionValue = "2.0.0", sdkValue = "flutter")
            val useCase = createUseCase(versionsRepo, FakeSettingsRepository(), devicesRepo)

            val result = useCase().first()
            assertIs<IsLastVersionDomainModel.NewVersionAvailable>(result)
            assertEquals("2.0.1", result.name)
            assertEquals(FloconSdk.FLUTTER, result.sdk)
            assertEquals("https://github.com/openflocon/FloconFlutter/releases/latest", result.link)
        }
    }

    @Test
    fun `an unknown SDK or an app that did not say bonjour yet never gets an update`() {
        runBlocking {
            val versionsRepo = FakeVersionsCheckerRepository(sdkVersions = mapOf(FloconSdk.KOTLIN to "9.0.0"))
            listOf("react-native", null).forEach { sdk ->
                val devicesRepo = FakeDevicesRepository(sdkVersionValue = "1.0.0", sdkValue = sdk)
                val useCase = createUseCase(versionsRepo, FakeSettingsRepository(), devicesRepo)

                assertIs<IsLastVersionDomainModel.RunningLastVersion>(useCase().first())
            }
            assertEquals(emptyList(), versionsRepo.checkedSdks)
        }
    }

    @Test
    fun `fromName knows the SDKs the bonjour message names`() {
        assertEquals(FloconSdk.KOTLIN, FloconSdk.fromName("kotlin"))
        assertEquals(FloconSdk.SWIFT, FloconSdk.fromName("Swift"))
        assertEquals(FloconSdk.FLUTTER, FloconSdk.fromName("flutter"))
        assertNull(FloconSdk.fromName("react-native"))
        assertNull(FloconSdk.fromName(null))
    }
}
