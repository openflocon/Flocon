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
import io.github.openflocon.domain.versions.repository.VersionsCheckerRepository
import io.github.openflocon.domain.versions.usecase.ObserveLastAvailableFloconVersionUseCase
import io.github.openflocon.domain.versions.usecase.ObserveUpdateAvailableUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ObserveUpdateAvailableUseCaseTest {

    private class FakeVersionsCheckerRepository(
        initialVersion: String? = null
    ) : VersionsCheckerRepository {
        val versionFlow = MutableStateFlow(initialVersion)
        override val lastVersion: Flow<String?> = versionFlow.asStateFlow()
        override suspend fun checkIsLastVersion(): Either<Throwable, String> = throw NotImplementedError()
    }

    private class FakeDevicesRepository(
        var currentDeviceIdValue: String? = "device1",
        var sdkVersionValue: String? = null,
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
    fun `when remote version is newer than desktop, emits DesktopUpdate`() {
        runBlocking {
            val versionsRepo = FakeVersionsCheckerRepository(initialVersion = "1.1.0")
            val devicesRepo = FakeDevicesRepository(sdkVersionValue = "1.0.0")
            val observeRemoteUseCase = ObserveLastAvailableFloconVersionUseCase(versionsRepo)
            val observeSdkUseCase = createObserveSdkUseCase(devicesRepo)

            val useCase = ObserveUpdateAvailableUseCase(
                observeLastAvailableFloconVersionUseCase = observeRemoteUseCase,
                observeCurrentDeviceFloconSdkVersionNameUseCase = observeSdkUseCase,
            )

            val result = useCase(desktopAppVersion = "1.0.0").first()
            assertIs<ObserveUpdateAvailableUseCase.UpdateAvailableDomainModel.DesktopUpdate>(result)
            assertEquals("1.1.0", result.version)
            assertEquals("https://github.com/openflocon/Flocon/releases/tag/1.1.0", result.link)
        }
    }

    @Test
    fun `when desktop is up to date and client is older, emits ClientUpdate`() {
        runBlocking {
            val versionsRepo = FakeVersionsCheckerRepository(initialVersion = "1.1.0")
            val devicesRepo = FakeDevicesRepository(sdkVersionValue = "1.0.0")
            val observeRemoteUseCase = ObserveLastAvailableFloconVersionUseCase(versionsRepo)
            val observeSdkUseCase = createObserveSdkUseCase(devicesRepo)

            val useCase = ObserveUpdateAvailableUseCase(
                observeLastAvailableFloconVersionUseCase = observeRemoteUseCase,
                observeCurrentDeviceFloconSdkVersionNameUseCase = observeSdkUseCase,
            )

            val result = useCase(desktopAppVersion = "1.1.0").first()
            assertIs<ObserveUpdateAvailableUseCase.UpdateAvailableDomainModel.ClientUpdate>(result)
            assertEquals("1.1.0", result.version)
            assertEquals("1.0.0", result.oldVersion)
        }
    }

    @Test
    fun `when both desktop and client are up to date, emits None`() {
        runBlocking {
            val versionsRepo = FakeVersionsCheckerRepository(initialVersion = "1.1.0")
            val devicesRepo = FakeDevicesRepository(sdkVersionValue = "1.1.0")
            val observeRemoteUseCase = ObserveLastAvailableFloconVersionUseCase(versionsRepo)
            val observeSdkUseCase = createObserveSdkUseCase(devicesRepo)

            val useCase = ObserveUpdateAvailableUseCase(
                observeLastAvailableFloconVersionUseCase = observeRemoteUseCase,
                observeCurrentDeviceFloconSdkVersionNameUseCase = observeSdkUseCase,
            )

            val result = useCase(desktopAppVersion = "1.1.0").first()
            assertIs<ObserveUpdateAvailableUseCase.UpdateAvailableDomainModel.None>(result)
        }
    }

    @Test
    fun `when remote is null, emits None`() {
        runBlocking {
            val versionsRepo = FakeVersionsCheckerRepository(initialVersion = null)
            val devicesRepo = FakeDevicesRepository(sdkVersionValue = "1.0.0")
            val observeRemoteUseCase = ObserveLastAvailableFloconVersionUseCase(versionsRepo)
            val observeSdkUseCase = createObserveSdkUseCase(devicesRepo)

            val useCase = ObserveUpdateAvailableUseCase(
                observeLastAvailableFloconVersionUseCase = observeRemoteUseCase,
                observeCurrentDeviceFloconSdkVersionNameUseCase = observeSdkUseCase,
            )

            val result = useCase(desktopAppVersion = "1.0.0").first()
            assertIs<ObserveUpdateAvailableUseCase.UpdateAvailableDomainModel.None>(result)
        }
    }
}
