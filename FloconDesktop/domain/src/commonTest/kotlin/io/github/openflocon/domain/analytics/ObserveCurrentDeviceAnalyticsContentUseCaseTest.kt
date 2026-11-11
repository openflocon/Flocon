package io.github.openflocon.domain.analytics

import androidx.paging.PagingData
import io.github.openflocon.domain.analytics.models.AnalyticsEventCountDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsIdentifierDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsItemDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsSearchDomainModel
import io.github.openflocon.domain.analytics.repository.AnalyticsPreferencesRepository
import io.github.openflocon.domain.analytics.repository.AnalyticsRepository
import io.github.openflocon.domain.analytics.usecase.ObserveCurrentDeviceAnalyticsContentUseCase
import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.models.HandleDeviceResultDomainModel
import io.github.openflocon.domain.device.models.RegisterDeviceWithAppDomainModel
import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveCurrentDeviceAnalyticsContentUseCaseTest {

    private val excludedByPackage = mutableMapOf(
        "app.a" to MutableStateFlow(setOf("heartbeat")),
        "app.b" to MutableStateFlow(emptySet()),
    )
    private val selectedPackage = MutableStateFlow("app.a")
    private val queried = mutableListOf<Set<String>>()

    private val useCase = ObserveCurrentDeviceAnalyticsContentUseCase(
        observeCurrentDeviceIdAndPackageNameUseCase = ObserveCurrentDeviceIdAndPackageNameUseCase(
            observeCurrentDeviceIdUseCase = ObserveCurrentDeviceIdUseCase(FakeDevicesRepository(selectedPackage)),
            devicesRepository = FakeDevicesRepository(selectedPackage),
        ),
        analyticsRepository = RecordingAnalyticsRepository(queried),
        analyticsPreferencesRepository = object : AnalyticsPreferencesRepository {
            override fun observeExcludedEventNames(packageName: String): Flow<Set<String>> = excludedByPackage.getValue(packageName)
            override suspend fun excludeEventName(packageName: String, eventName: String) = Unit
            override suspend fun restoreEventName(packageName: String, eventName: String) = Unit
            override suspend fun restoreAllEventNames(packageName: String) = Unit
        },
    )

    @Test
    fun `passes the excluded names of the current package to the repository`() = runBlocking {
        useCase(filter = null).first()

        assertEquals(setOf("heartbeat"), queried.last())
    }

    @Test
    fun `an empty excluded set is passed as is`() = runBlocking {
        selectedPackage.value = "app.b"

        useCase(filter = null).first()

        assertEquals(emptySet(), queried.last())
    }

    @Test
    fun `re-queries when the excluded set of the current package changes`() = runBlocking {
        val job = launch(Dispatchers.Unconfined) { useCase(filter = null).collect {} }

        excludedByPackage.getValue("app.a").value = setOf("heartbeat", "scroll")
        job.cancel()

        assertEquals(listOf(setOf("heartbeat"), setOf("heartbeat", "scroll")), queried)
    }

    @Test
    fun `follows the current package when it changes`() = runBlocking {
        val job = launch(Dispatchers.Unconfined) { useCase(filter = null).collect {} }

        selectedPackage.value = "app.b"
        job.cancel()

        assertEquals(listOf(setOf("heartbeat"), emptySet()), queried)
    }
}

private class FakeDevicesRepository(
    private val selectedPackage: Flow<String>,
) : DevicesRepository {
    override val devices: Flow<List<DeviceDomainModel>> get() = unused()
    override val currentDeviceId: Flow<String?> = flowOf("device")
    override val activeDevices: Flow<Set<DeviceIdAndPackageNameDomainModel>> get() = unused()

    override fun observeDeviceSelectedApp(deviceId: String): Flow<DeviceAppDomainModel?> = kotlinx.coroutines.flow.flow {
        selectedPackage.collect { emit(DeviceAppDomainModel(name = it, packageName = it, iconEncoded = null, lastAppInstance = 0L, floconVersionOnDevice = "")) }
    }

    override suspend fun getCurrentDeviceId(): String? = unused()
    override suspend fun register(registerDeviceWithApp: RegisterDeviceWithAppDomainModel): HandleDeviceResultDomainModel = unused()
    override suspend fun getCurrentDevice(): DeviceDomainModel? = unused()
    override suspend fun selectDevice(deviceId: String) = unused()
    override fun observeDeviceApps(deviceId: String): Flow<List<DeviceAppDomainModel>> = unused()
    override suspend fun getDeviceSelectedApp(deviceId: String): DeviceAppDomainModel? = unused()
    override suspend fun getDeviceAppByPackage(deviceId: String, appPackageName: String): DeviceAppDomainModel? = unused()
    override suspend fun selectApp(deviceId: String, app: DeviceAppDomainModel) = unused()
    override suspend fun saveAppIcon(deviceId: String, appPackageName: String, iconEncoded: String) = unused()
    override suspend fun hasAppIcon(deviceId: String, appPackageName: String): Boolean = unused()
    override suspend fun askForDeviceAppIcon(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) = unused()
    override fun observeDeviceSdkVersion(deviceId: String, appPackageName: String): Flow<String?> = unused()
    override suspend fun deleteDevice(deviceId: String) = unused()
    override suspend fun deleteApplication(deviceId: String, packageName: String) = unused()
    override suspend fun clear() = unused()
    override fun observeCurrentDevice(): Flow<DeviceDomainModel?> = unused()
}

private class RecordingAnalyticsRepository(
    private val queried: MutableList<Set<String>>,
) : AnalyticsRepository {
    override fun observeAnalytics(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsTableId: String,
        filter: String?,
        excludedEventNames: Set<String>,
    ): Flow<PagingData<AnalyticsItemDomainModel>> {
        queried += excludedEventNames
        return flowOf(PagingData.empty())
    }

    override fun observeSelectedDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<AnalyticsIdentifierDomainModel?> = flowOf(AnalyticsIdentifierDomainModel(id = "t", name = "t"))

    override suspend fun getAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, analyticsTableId: String, filter: String?): List<AnalyticsItemDomainModel> = unused()
    override suspend fun searchAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, search: AnalyticsSearchDomainModel, newestFirst: Boolean, limit: Int): List<AnalyticsItemDomainModel> = unused()
    override suspend fun countAnalyticsByEventName(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, search: AnalyticsSearchDomainModel): List<AnalyticsEventCountDomainModel> = unused()
    override suspend fun deleteAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, analyticsId: AnalyticsIdentifierDomainModel) = unused()
    override fun observeAnalyticsById(id: String, deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<AnalyticsItemDomainModel?> = unused()
    override suspend fun selectDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, analyticsTableId: String) = unused()
    override fun observeDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<AnalyticsIdentifierDomainModel>> = unused()
    override suspend fun deleteAnalyticsItem(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, analyticsItemId: String) = unused()
    override suspend fun deleteBefore(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, analyticsItemId: String) = unused()
    override suspend fun deleteOldSessions(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) = unused()
}

private fun unused(): Nothing = error("not used by this test")
