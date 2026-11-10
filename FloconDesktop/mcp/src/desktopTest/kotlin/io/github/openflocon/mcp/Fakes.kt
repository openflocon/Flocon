package io.github.openflocon.mcp

import androidx.paging.PagingData
import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.models.HandleDeviceResultDomainModel
import io.github.openflocon.domain.device.models.RegisterDeviceWithAppDomainModel
import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdUseCase
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.NetworkCallWithDeviceId
import io.github.openflocon.domain.network.models.NetworkFilterDomainModel
import io.github.openflocon.domain.network.models.NetworkSortDomainModel
import io.github.openflocon.domain.screen.models.FloconScreen
import io.github.openflocon.domain.screen.models.ScreenRequestDomainModel
import io.github.openflocon.domain.screen.repository.DesktopNavigationRepository
import io.github.openflocon.domain.timeline.models.TimelineEventDomainModel
import io.github.openflocon.domain.timeline.models.TimelineEventRef
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.models.TimelineFocusRequestDomainModel
import io.github.openflocon.domain.timeline.models.TimelinePhase
import io.github.openflocon.domain.timeline.models.TimelineSettingsDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.models.TimelineSourceSummaryDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSummaryDomainModel
import io.github.openflocon.domain.timeline.repository.TimelineFocusRepository
import io.github.openflocon.domain.timeline.repository.TimelinePreferencesRepository
import io.github.openflocon.domain.timeline.repository.TimelineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

internal fun app(
    packageName: String,
    lastAppInstance: Long = 1L,
) = DeviceAppDomainModel(
    name = packageName.substringAfterLast('.'),
    packageName = packageName,
    iconEncoded = null,
    lastAppInstance = lastAppInstance,
    floconVersionOnDevice = "1.0.0",
)

internal fun httpCall(
    callId: String,
    url: String = "https://api.flocon.io/users",
    method: String = "GET",
    startTime: Long = 1_000L,
    appInstance: Long = 1L,
    httpCode: Int? = 200,
    requestBody: String? = null,
    responseBody: String? = """{"name":"flocon"}""",
) = FloconNetworkCallDomainModel(
    callId = callId,
    appInstance = appInstance,
    request = FloconNetworkCallDomainModel.Request(
        url = url,
        startTime = startTime,
        startTimeFormatted = startTime.toString(),
        method = method,
        methodFormatted = method,
        headers = mapOf("Authorization" to "Bearer token"),
        body = requestBody,
        byteSize = requestBody?.length?.toLong() ?: 0L,
        byteSizeFormatted = "",
        isMocked = false,
        specificInfos = FloconNetworkCallDomainModel.Request.SpecificInfos.Http,
        domainFormatted = "api.flocon.io",
        queryFormatted = "/users",
    ),
    response = httpCode?.let {
        FloconNetworkCallDomainModel.Response.Success(
            durationMs = 12.0,
            durationFormatted = "12ms",
            contentType = "application/json",
            body = responseBody,
            headers = mapOf("Content-Type" to "application/json"),
            byteSize = responseBody?.length?.toLong() ?: 0L,
            byteSizeFormatted = "",
            specificInfos = FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Http(httpCode = it),
            isImage = false,
            statusFormatted = it.toString(),
        )
    },
)

internal class FakeDevicesRepository(
    private val devicesWithApps: Map<DeviceDomainModel, List<DeviceAppDomainModel>>,
    var currentDeviceIdValue: DeviceId? = null,
    var selectedApps: Map<DeviceId, String> = emptyMap(),
    activeDevicesValue: Set<DeviceIdAndPackageNameDomainModel> = emptySet(),
) : DevicesRepository {
    override val devices: Flow<List<DeviceDomainModel>> = flowOf(devicesWithApps.keys.toList())
    override val currentDeviceId: Flow<DeviceId?> = MutableStateFlow(currentDeviceIdValue)
    override val activeDevices: Flow<Set<DeviceIdAndPackageNameDomainModel>> = flowOf(activeDevicesValue)

    private fun appsOf(deviceId: DeviceId) = devicesWithApps.entries.firstOrNull { it.key.deviceId == deviceId }?.value.orEmpty()

    override suspend fun getCurrentDeviceId(): DeviceId? = currentDeviceIdValue
    override suspend fun getCurrentDevice(): DeviceDomainModel? = devicesWithApps.keys.firstOrNull { it.deviceId == currentDeviceIdValue }
    override fun observeDeviceApps(deviceId: DeviceId): Flow<List<DeviceAppDomainModel>> = flowOf(appsOf(deviceId))
    override fun observeDeviceSelectedApp(deviceId: DeviceId): Flow<DeviceAppDomainModel?> = flowOf(Unit).map { getDeviceSelectedApp(deviceId) }

    override suspend fun getDeviceSelectedApp(deviceId: DeviceId): DeviceAppDomainModel? = selectedApps[deviceId]?.let { getDeviceAppByPackage(deviceId, it) }

    override suspend fun getDeviceAppByPackage(deviceId: DeviceId, appPackageName: String): DeviceAppDomainModel? = appsOf(deviceId).firstOrNull { it.packageName == appPackageName }

    override fun observeCurrentDevice(): Flow<DeviceDomainModel?> = flowOf(null)
    override fun observeDeviceSdkVersion(deviceId: DeviceId, appPackageName: String): Flow<String?> = flowOf(null)

    override suspend fun register(registerDeviceWithApp: RegisterDeviceWithAppDomainModel): HandleDeviceResultDomainModel = error("not used")

    /** What was selected in Flocon, in order: "device:<id>" then "app:<id>/<package>" */
    val selections = mutableListOf<String>()

    override suspend fun selectDevice(deviceId: DeviceId) {
        selections += "device:$deviceId"
        currentDeviceIdValue = deviceId
    }

    override suspend fun selectApp(deviceId: DeviceId, app: DeviceAppDomainModel) {
        selections += "app:$deviceId/${app.packageName}"
        selectedApps = selectedApps + (deviceId to app.packageName)
    }

    override suspend fun saveAppIcon(deviceId: DeviceId, appPackageName: String, iconEncoded: String) = error("not used")
    override suspend fun hasAppIcon(deviceId: DeviceId, appPackageName: String): Boolean = error("not used")
    override suspend fun askForDeviceAppIcon(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) = error("not used")
    override suspend fun deleteDevice(deviceId: DeviceId) = error("not used")
    override suspend fun deleteApplication(deviceId: DeviceId, packageName: String) = error("not used")
    override suspend fun clear() = error("not used")
}

/**
 * Stores calls per device / app, sorting & session filtering like the Room implementation
 */
internal class FakeNetworkRepository(
    private val calls: Map<Pair<String, String>, List<FloconNetworkCallDomainModel>>,
) : io.github.openflocon.domain.network.repository.NetworkRepository {

    private fun callsOf(target: DeviceIdAndPackageNameDomainModel) = calls[target.deviceId to target.packageName].orEmpty()

    override fun observeHasRequests(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) = flowOf(callsOf(deviceIdAndPackageName).isNotEmpty())

    override suspend fun getRequests(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sortedBy: NetworkSortDomainModel?,
        filter: NetworkFilterDomainModel,
    ): List<FloconNetworkCallDomainModel> = callsOf(deviceIdAndPackageName)
        .filter { filter.displayOldSessions || it.appInstance == deviceIdAndPackageName.appInstance }
        .filter { call -> filter.filterOnAllColumns?.let { call.request.url.contains(it, ignoreCase = true) } ?: true }
        .sortedBy { it.request.startTime }
        .let { if (sortedBy?.asc == false) it.reversed() else it }

    override suspend fun getRequests(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        ids: List<String>,
    ): List<FloconNetworkCallDomainModel> = callsOf(deviceIdAndPackageName).filter { it.callId in ids }

    override suspend fun addCalls(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, calls: List<FloconNetworkCallDomainModel>) = error("not used")
    override fun observeRequests(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sortedBy: NetworkSortDomainModel?,
        filter: NetworkFilterDomainModel,
    ): Flow<PagingData<FloconNetworkCallDomainModel>> = error("not used")

    override fun observeRequest(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, requestId: String): Flow<FloconNetworkCallDomainModel?> = error("not used")
    override suspend fun clear() = error("not used")
    override suspend fun clearDeviceCalls(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) = error("not used")
    override suspend fun deleteRequest(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, requestId: String) = error("not used")
    override suspend fun deleteRequestsBefore(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, requestId: String) = error("not used")
    override suspend fun deleteOldRequests(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) = error("not used")
    override suspend fun getAllNetworkCalls(deviceId: String?, startTimestamp: Long?, endTimestamp: Long?): List<NetworkCallWithDeviceId> = error("not used")
    override suspend fun replayRequest(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, request: FloconNetworkCallDomainModel) = error("not used")
}

internal fun targetResolver(devicesRepository: DevicesRepository) = McpTargetResolver(
    devicesRepository = devicesRepository,
    getCurrentDeviceIdAndPackageNameUseCase = GetCurrentDeviceIdAndPackageNameUseCase(
        getCurrentDeviceIdUseCase = GetCurrentDeviceIdUseCase(devicesRepository),
        devicesRepository = devicesRepository,
    ),
)

internal class FakeDesktopNavigationRepository : DesktopNavigationRepository {
    val requestedScreens = mutableListOf<FloconScreen>()

    override fun requestScreen(screen: FloconScreen) {
        requestedScreens += screen
    }

    override fun observeRequestedScreen(): Flow<ScreenRequestDomainModel> = emptyFlow()

    override fun consumeRequestedScreen(request: ScreenRequestDomainModel) = Unit
}

/** The events of the timeline, filtered like the Room query: sources, time bounds and event refs (a call is its request event) */
internal class FakeTimelineRepository(private val events: List<TimelineEventDomainModel>) : TimelineRepository {

    override fun observeSummary(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        filter: TimelineFilterDomainModel,
    ): Flow<TimelineSummaryDomainModel> = flowOf(
        TimelineSummaryDomainModel(
            sources = events.groupBy { it.source }.mapValues { (_, events) ->
                TimelineSourceSummaryDomainModel(
                    count = events.size,
                    httpErrorCount = events.count { (it.statusCode ?: 0) >= 400 },
                    firstTimestamp = events.minOf { it.timestamp },
                    lastTimestamp = events.maxOf { it.timestamp },
                )
            },
            sessionEventCounts = emptyMap(),
        )
    )

    override fun observeTimelineEvents(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        filter: TimelineFilterDomainModel,
    ): Flow<List<TimelineEventDomainModel>> = flowOf(
        events
            .filter { it.source in filter.sources }
            .filter { filter.fromMs == null || it.timestamp >= filter.fromMs!! }
            .filter { filter.toMs == null || it.timestamp <= filter.toMs!! }
            .filter { event ->
                filter.events == null || (event.phase == TimelinePhase.Request && TimelineEventRef(event.source, event.id) in filter.events!!)
            }
            .sortedByDescending { it.timestamp }
    )

    override fun observeTimeline(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        filter: TimelineFilterDomainModel,
    ): Flow<PagingData<TimelineEventDomainModel>> = error("not used")
}

internal class FakeTimelinePreferencesRepository : TimelinePreferencesRepository {
    val graphCalls = mutableListOf<Boolean>()

    override val settings: Flow<TimelineSettingsDomainModel> = MutableStateFlow(TimelineSettingsDomainModel())

    override suspend fun setGraph(graph: Boolean) {
        graphCalls += graph
    }

    override suspend fun setMergePlugins(mergePlugins: Boolean) = error("not used")
    override suspend fun setAutoScroll(autoScroll: Boolean) = error("not used")
    override suspend fun setInvertList(invertList: Boolean) = error("not used")
    override suspend fun setPinPanel(pinPanel: Boolean) = error("not used")
    override suspend fun setSelectedSources(sources: Set<TimelineSource>) = error("not used")
    override suspend fun setScreenshots(screenshots: Boolean) = error("not used")
    override suspend fun setScreenshotIntervalSeconds(seconds: Int) = error("not used")
}

internal class FakeTimelineFocusRepository : TimelineFocusRepository {
    val requests = mutableListOf<TimelineFocusRequestDomainModel>()

    override fun requestFocus(
        target: DeviceIdAndPackageNameDomainModel,
        sessionStart: Long?,
        event: TimelineEventRef?,
        fromMs: Long,
        toMs: Long,
    ) {
        requests += TimelineFocusRequestDomainModel(target, sessionStart, event, fromMs, toMs, requestedAtMs = 0L)
    }

    override fun observeFocus(): Flow<TimelineFocusRequestDomainModel> = emptyFlow()

    override fun consumeFocus(request: TimelineFocusRequestDomainModel) = Unit
}
