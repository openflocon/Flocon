package com.flocon.data.remote.mock

import com.flocon.data.remote.accessibility.model.AccessibilityItemDataModel
import com.flocon.data.remote.accessibility.model.AccessibilityStateDataModel
import com.flocon.data.remote.analytics.model.AnalyticsItemDataModel
import com.flocon.data.remote.analytics.model.AnalyticsPropertyDataModel
import com.flocon.data.remote.crashreporter.model.CrashReportDataModel
import com.flocon.data.remote.dashboard.models.ButtonConfigDataModel
import com.flocon.data.remote.dashboard.models.CheckBoxConfigDataModel
import com.flocon.data.remote.dashboard.models.DashboardConfigDataModel
import com.flocon.data.remote.dashboard.models.DashboardContainerDataModel
import com.flocon.data.remote.dashboard.models.DashboardElementDataModel
import com.flocon.data.remote.dashboard.models.FormContainerConfigDataModel
import com.flocon.data.remote.dashboard.models.HtmlConfigDataModel
import com.flocon.data.remote.dashboard.models.LabelConfigDataModel
import com.flocon.data.remote.dashboard.models.MarkdownConfigDataModel
import com.flocon.data.remote.dashboard.models.PlainTextConfigDataModel
import com.flocon.data.remote.dashboard.models.SectionContainerConfigDataModel
import com.flocon.data.remote.dashboard.models.TextConfigDataModel
import com.flocon.data.remote.dashboard.models.TextFieldConfigDataModel
import com.flocon.data.remote.database.models.DatabaseQueryLogModel
import com.flocon.data.remote.database.models.DeviceDataBaseDataModel
import com.flocon.data.remote.deeplink.models.DeeplinkReceivedDataModel
import com.flocon.data.remote.deeplink.models.DeeplinkVariableReceivedDataModel
import com.flocon.data.remote.deeplink.models.DeeplinksReceivedDataModel
import com.flocon.data.remote.featureflags.model.FeatureFlagDataModel
import com.flocon.data.remote.featureflags.model.FeatureFlagsDataModel
import com.flocon.data.remote.files.models.FromDeviceFilesRootsDataModel
import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import com.flocon.data.remote.network.models.FloconNetworkRequestDataModel
import com.flocon.data.remote.network.models.FloconNetworkResponseDataModel
import com.flocon.data.remote.sharedpreference.models.DeviceSharedPreferenceDataModel
import com.flocon.data.remote.table.model.TableColumnDataModel
import com.flocon.data.remote.table.model.TableItemDataModel
import com.flocon.data.remote.timelinelog.model.TimelineLogDataModel
import com.flocon.data.remote.webview.model.WebViewBridgeCallDataModel
import com.flocon.data.remote.webview.model.WebViewConsoleLogDataModel
import com.flocon.data.remote.webview.model.WebViewPageEventDataModel
import io.github.openflocon.domain.Protocol
import kotlinx.serialization.json.Json
import kotlin.time.Instant

/**
 * Turns a [MockScenario] into the messages a real SDK would send, using the same wire models
 * as the remote data sources, so the mock goes through the real decoding path.
 */
internal class MockMessageEncoder(private val json: Json) {

    fun encode(
        scenario: MockScenario,
        launchTime: Instant,
        defaultSdkVersion: String,
    ): List<FloconIncomingMessageDataModel> = scenario.devices
        .flatMap { device ->
            val appInstance = device.appInstance(launchTime)
            device.events.flatMapIndexed { index, event ->
                val timestamp = (launchTime - event.at).toEpochMilliseconds()
                encodeEvent(device, index, event, timestamp).map { (plugin, method, body) ->
                    timestamp to device.message(plugin = plugin, method = method, body = body, appInstance = appInstance, defaultSdkVersion = defaultSdkVersion)
                }
            }
        }
        .sortedBy { it.first } // stable: a request stays before its response
        .map { it.second }

    private fun encodeEvent(
        device: MockDevice,
        index: Int,
        event: MockEvent,
        timestamp: Long,
    ): List<Triple<String, String, String>> = when (event) {
        is MockEvent.NetworkCall -> encodeNetworkCall(callId = "mock-${device.id}-$index", call = event, timestamp = timestamp)

        is MockEvent.TimelineLog -> listOf(
            Triple(
                Protocol.FromDevice.Timeline.Plugin,
                Protocol.FromDevice.Timeline.Method.Log,
                json.encodeToString(
                    TimelineLogDataModel(
                        type = event.type,
                        value = event.value,
                        params = event.params,
                        level = event.level,
                        timestamp = timestamp,
                    ),
                ),
            ),
        )

        is MockEvent.Crash -> listOf(
            Triple(
                Protocol.FromDevice.CrashReporter.Plugin,
                Protocol.FromDevice.CrashReporter.Method.ReportCrash,
                json.encodeToString(
                    listOf(
                        CrashReportDataModel(
                            crashId = "mock-crash-${device.id}-$index",
                            timestamp = timestamp,
                            exceptionType = event.exceptionType,
                            exceptionMessage = event.message,
                            stackTrace = event.stackTrace,
                        ),
                    ),
                ),
            ),
        )

        is MockEvent.AnalyticsEvent -> listOf(
            Triple(
                Protocol.FromDevice.Analytics.Plugin,
                Protocol.FromDevice.Analytics.Method.AddItems,
                json.encodeToString(
                    listOf(
                        AnalyticsItemDataModel(
                            id = "mock-analytics-${device.id}-$index",
                            analyticsTableId = event.tableId,
                            eventName = event.eventName,
                            createdAt = timestamp,
                            properties = event.properties.map { (name, value) -> AnalyticsPropertyDataModel(name = name, value = value) },
                        ),
                    ),
                ),
            ),
        )

        is MockEvent.FeatureFlags -> listOf(
            Triple(
                Protocol.FromDevice.FeatureFlags.Plugin,
                Protocol.FromDevice.FeatureFlags.Method.Register,
                json.encodeToString(
                    FeatureFlagsDataModel(
                        provider = event.provider,
                        flags = event.flags.map {
                            FeatureFlagDataModel(name = it.name, state = it.state, variant = it.variant, payload = it.payload)
                        },
                    ),
                ),
            ),
        )

        is MockEvent.Deeplinks -> listOf(
            Triple(
                Protocol.FromDevice.Deeplink.Plugin,
                Protocol.FromDevice.Deeplink.Method.GetDeeplinks,
                json.encodeToString(
                    DeeplinksReceivedDataModel(
                        deeplinks = event.deeplinks.map { deeplink ->
                            DeeplinkReceivedDataModel(
                                label = deeplink.label,
                                link = deeplink.link,
                                description = deeplink.description,
                                parameters = deeplink.parameters.map {
                                    when (it) {
                                        is MockEvent.Deeplinks.Parameter.AutoComplete -> DeeplinkReceivedDataModel.Parameter.AutoComplete(it.name, it.values)
                                        is MockEvent.Deeplinks.Parameter.Variable -> DeeplinkReceivedDataModel.Parameter.Variable(it.name, it.variableName)
                                    }
                                },
                            )
                        },
                        variables = event.variables.map {
                            DeeplinkVariableReceivedDataModel(
                                name = it.name,
                                mode = it.suggestions?.let(DeeplinkVariableReceivedDataModel.Mode::AutoComplete)
                                    ?: DeeplinkVariableReceivedDataModel.Mode.Input,
                                description = it.description,
                            )
                        },
                    ),
                ),
            ),
        )

        is MockEvent.Databases -> listOf(
            Triple(
                Protocol.FromDevice.Database.Plugin,
                Protocol.FromDevice.Database.Method.GetDatabases,
                json.encodeToString(event.databases.map { DeviceDataBaseDataModel(id = it.id, name = it.name) }),
            ),
        )

        is MockEvent.DatabaseQueryLog -> listOf(
            Triple(
                Protocol.FromDevice.Database.Plugin,
                Protocol.FromDevice.Database.Method.LogQuery,
                json.encodeToString(
                    DatabaseQueryLogModel(
                        dbId = event.databaseId,
                        sqlQuery = event.sql,
                        bindArgs = event.bindArgs,
                        timestamp = timestamp,
                        transactionId = event.transactionId,
                    ),
                ),
            ),
        )

        // the SDK only announces its roots when asked (MockDeviceResponder answers); this unrequested answer ignored by the desktop
        // is there so that a device with only files still registers (a device exists from its first message)
        is MockEvent.Files -> listOf(
            Triple(
                Protocol.FromDevice.Files.Plugin,
                Protocol.FromDevice.Files.Method.GetRoots,
                json.encodeToString(
                    FromDeviceFilesRootsDataModel(
                        requestId = "mock-roots-$index",
                        roots = event.roots.map { FromDeviceFilesRootsDataModel.Root(id = it.id, name = it.name, path = it.path) },
                    ),
                ),
            ),
        )

        is MockEvent.SharedPreferences -> listOf(
            Triple(
                Protocol.FromDevice.SharedPreferences.Plugin,
                Protocol.FromDevice.SharedPreferences.Method.GetSharedPreferences,
                json.encodeToString(event.preferences.sortedBy { it.name }.map { DeviceSharedPreferenceDataModel(name = it.name, type = it.type) }),
            ),
        )

        is MockEvent.Dashboard -> listOf(
            Triple(
                Protocol.FromDevice.Dashboard.Plugin,
                Protocol.FromDevice.Dashboard.Method.Update,
                json.encodeToString(event.toDataModel()),
            ),
        )

        is MockEvent.TableRow -> listOf(
            Triple(
                Protocol.FromDevice.Table.Plugin,
                Protocol.FromDevice.Table.Method.AddItems,
                json.encodeToString(
                    listOf(
                        TableItemDataModel(
                            id = "mock-table-${device.id}-$index",
                            name = event.tableName,
                            createdAt = timestamp,
                            columns = event.cells.map { (column, value) -> TableColumnDataModel(column = column, value = value) },
                        ),
                    ),
                ),
            ),
        )

        is MockEvent.AccessibilityState -> listOf(
            Triple(
                Protocol.FromDevice.Accessibility.Plugin,
                Protocol.FromDevice.Accessibility.Method.State,
                json.encodeToString(
                    AccessibilityStateDataModel(
                        supported = event.supported,
                        enabled = event.enabled,
                        touchExploration = event.touchExploration,
                        services = event.services,
                        canToggleTalkBack = event.canToggleTalkBack,
                    ),
                ),
            ),
        )

        is MockEvent.AccessibilityEvent -> listOf(
            Triple(
                Protocol.FromDevice.Accessibility.Plugin,
                Protocol.FromDevice.Accessibility.Method.AddEvents,
                json.encodeToString(
                    listOf(
                        AccessibilityItemDataModel(
                            id = "mock-a11y-${device.id}-$index",
                            type = event.type,
                            text = event.text,
                            contentDescription = event.contentDescription,
                            className = event.className,
                            screen = event.screen,
                            bounds = event.bounds,
                            extras = event.extras,
                            createdAt = timestamp,
                        ),
                    ),
                ),
            ),
        )

        is MockEvent.WebViewConsole -> listOf(
            Triple(
                Protocol.FromDevice.WebView.Plugin,
                Protocol.FromDevice.WebView.Method.AddConsoleLogs,
                json.encodeToString(
                    listOf(
                        WebViewConsoleLogDataModel(
                            id = "mock-webview-console-${device.id}-$index",
                            webViewId = event.webViewId,
                            url = event.url,
                            level = event.level,
                            message = event.message,
                            sourceId = event.sourceId,
                            lineNumber = event.lineNumber,
                            createdAt = timestamp,
                        ),
                    ),
                ),
            ),
        )

        is MockEvent.WebViewPage -> listOf(
            Triple(
                Protocol.FromDevice.WebView.Plugin,
                Protocol.FromDevice.WebView.Method.AddPageEvents,
                json.encodeToString(
                    listOf(
                        WebViewPageEventDataModel(
                            id = "mock-webview-page-${device.id}-$index",
                            webViewId = event.webViewId,
                            url = event.url,
                            type = event.type,
                            message = event.message,
                            statusCode = event.statusCode,
                            createdAt = timestamp,
                        ),
                    ),
                ),
            ),
        )

        is MockEvent.WebViewBridge -> listOf(
            Triple(
                Protocol.FromDevice.WebView.Plugin,
                Protocol.FromDevice.WebView.Method.AddBridgeCalls,
                json.encodeToString(
                    listOf(
                        WebViewBridgeCallDataModel(
                            id = "mock-webview-bridge-${device.id}-$index",
                            webViewId = event.webViewId,
                            url = event.url,
                            interfaceName = event.interfaceName,
                            method = event.method,
                            arguments = event.arguments,
                            result = event.result,
                            createdAt = timestamp,
                        ),
                    ),
                ),
            ),
        )

        is MockEvent.Raw -> listOf(Triple(event.plugin, event.method, event.body))
    }

    private fun encodeNetworkCall(
        callId: String,
        call: MockEvent.NetworkCall,
        timestamp: Long,
    ): List<Triple<String, String, String>> {
        val request = Triple(
            Protocol.FromDevice.Network.Plugin,
            Protocol.FromDevice.Network.Method.LogNetworkCallRequest,
            json.encodeToString(
                FloconNetworkRequestDataModel(
                    floconCallId = callId,
                    floconNetworkType = if (call.grpcStatus != null) "grpc" else "http",
                    url = call.url,
                    method = call.method,
                    startTime = timestamp,
                    requestHeaders = call.requestHeaders,
                    requestBody = call.requestBody,
                    requestSize = call.requestBody?.encodeToByteArray()?.size?.toLong() ?: 0L,
                    isMocked = false,
                ),
            ),
        )
        if (call.pending) return listOf(request)

        val responseContentType = call.contentType ?: call.responseHeaders.entries
            .firstOrNull { it.key.equals("content-type", ignoreCase = true) }?.value
        val response = Triple(
            Protocol.FromDevice.Network.Plugin,
            Protocol.FromDevice.Network.Method.LogNetworkCallResponse,
            json.encodeToString(
                FloconNetworkResponseDataModel(
                    floconCallId = callId,
                    floconNetworkType = if (call.grpcStatus != null) "grpc" else "http",
                    durationMs = call.duration.inWholeMicroseconds / 1000.0,
                    responseHttpCode = call.status.takeIf { call.error == null && call.grpcStatus == null },
                    responseGrpcStatus = call.grpcStatus,
                    responseContentType = responseContentType,
                    isImage = responseContentType?.startsWith("image/", ignoreCase = true) == true,
                    responseBody = call.responseBody,
                    responseHeaders = call.responseHeaders,
                    responseSize = call.responseSize ?: call.responseBody?.encodeToByteArray()?.size?.toLong() ?: 0L,
                    responseError = call.error,
                ),
            ),
        )
        return listOf(request, response)
    }

    internal fun MockDevice.message(
        plugin: String,
        method: String,
        body: String,
        appInstance: Long,
        defaultSdkVersion: String,
    ) = FloconIncomingMessageDataModel(
        deviceName = name,
        deviceId = id,
        plugin = plugin,
        body = body,
        method = method,
        appName = appName,
        appPackageName = packageName,
        appInstance = appInstance,
        platform = platform,
        versionName = sdkVersion ?: defaultSdkVersion,
    )
}

private fun MockEvent.Dashboard.toDataModel() = DashboardConfigDataModel(
    dashboardId = dashboardId,
    containers = containers.map { container ->
        DashboardContainerDataModel(
            name = container.name,
            elements = container.elements.map { it.toDataModel() },
            containerConfig = if (container.formId != null) {
                FormContainerConfigDataModel(formId = container.formId, submitText = container.submitText ?: "Submit")
            } else {
                SectionContainerConfigDataModel
            },
        )
    },
)

private fun MockEvent.Dashboard.Element.toDataModel() = when (this) {
    is MockEvent.Dashboard.Element.Button -> DashboardElementDataModel(button = ButtonConfigDataModel(text = text, id = id))
    is MockEvent.Dashboard.Element.Label -> DashboardElementDataModel(label = LabelConfigDataModel(label = label, color = color))
    is MockEvent.Dashboard.Element.Text -> DashboardElementDataModel(text = TextConfigDataModel(label = label, value = value, color = color))
    is MockEvent.Dashboard.Element.TextField -> DashboardElementDataModel(textField = TextFieldConfigDataModel(label = label, placeHolder = placeHolder, value = value, id = id))
    is MockEvent.Dashboard.Element.CheckBox -> DashboardElementDataModel(checkBox = CheckBoxConfigDataModel(label = label, value = value, id = id))
    is MockEvent.Dashboard.Element.PlainText -> DashboardElementDataModel(plainText = PlainTextConfigDataModel(label = label, value = value, type = if (json) "json" else "text"))
    is MockEvent.Dashboard.Element.Markdown -> DashboardElementDataModel(markdown = MarkdownConfigDataModel(label = label, value = value))
    is MockEvent.Dashboard.Element.Html -> DashboardElementDataModel(html = HtmlConfigDataModel(label = label, value = value))
}
