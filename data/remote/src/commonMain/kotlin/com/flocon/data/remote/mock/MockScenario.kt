package com.flocon.data.remote.mock

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

/**
 * Data replayed by the mock mode (`--mock=<name>`) as if devices were connected.
 * Written with the DSL in `MockScenarioDsl.kt`, listed in [MockScenarios].
 */
data class MockScenario(
    val name: String,
    val description: String,
    val devices: List<MockDevice>,
)

data class MockDevice(
    val id: String,
    val name: String,
    val platform: String, // "android" or "ios"
    val appName: String,
    val packageName: String,
    val appStartedAt: Duration?, // how long before the launch the app started, null = just before its first event
    val sdkVersion: String?, // the Flocon SDK version on the device, null = the desktop version
    val events: List<MockEvent>,
)

/**
 * The SDK start timestamp (ms) that identifies the app launch: the desktop draws the session from it.
 * By default the app started 1 s before its earliest event.
 */
internal fun MockDevice.appInstance(launchTime: Instant): Long {
    val startedAt = appStartedAt ?: ((events.maxOfOrNull { it.at } ?: Duration.ZERO) + 1.seconds)
    return (launchTime - startedAt).toEpochMilliseconds()
}

sealed interface MockEvent {
    /** How long before the app launch the event happened. */
    val at: Duration

    data class NetworkCall(
        override val at: Duration,
        val method: String,
        val url: String,
        val status: Int = 200,
        val duration: Duration = 100.milliseconds,
        val requestHeaders: Map<String, String> = emptyMap(),
        val requestBody: String? = null,
        val responseHeaders: Map<String, String> = emptyMap(),
        val responseBody: String? = null,
        val responseSize: Long? = null, // bytes, when the body is not kept (an image): null = the size of the body
        val contentType: String? = null,
        val error: String? = null, // the call failed without an http response (timeout, no network…)
        val pending: Boolean = false, // no response yet
        val grpcStatus: String? = null, // set for a gRPC call: [url] is the server authority and [method] the full method name
    ) : MockEvent

    data class TimelineLog(
        override val at: Duration,
        val value: String,
        val type: String = "log",
        val level: String? = "info",
        val params: Map<String, String> = emptyMap(),
    ) : MockEvent

    data class Crash(
        override val at: Duration,
        val exceptionType: String,
        val message: String,
        val stackTrace: String = "",
    ) : MockEvent

    data class AnalyticsEvent(
        override val at: Duration,
        val eventName: String,
        val tableId: String = "analytics",
        val properties: Map<String, String> = emptyMap(),
    ) : MockEvent

    data class FeatureFlags(
        override val at: Duration,
        val provider: String,
        val flags: List<Flag>,
    ) : MockEvent {
        data class Flag(
            val name: String,
            val state: String, // "on", "off" or "custom"
            val variant: String,
            val payload: String? = null,
        )
    }

    /** The deeplinks (and their variables) the app declares. */
    data class Deeplinks(
        override val at: Duration,
        val deeplinks: List<Deeplink>,
        val variables: List<Variable>,
    ) : MockEvent {
        data class Deeplink(
            val link: String,
            val label: String? = null,
            val description: String? = null,
            val parameters: List<Parameter> = emptyList(),
        )

        sealed interface Parameter {
            val name: String

            data class AutoComplete(override val name: String, val values: List<String>) : Parameter

            data class Variable(override val name: String, val variableName: String) : Parameter
        }

        data class Variable(
            val name: String,
            val description: String? = null,
            val suggestions: List<String>? = null, // null = free input
        )
    }

    /** The databases of the app, with their tables: the mock answers the SQL queries sent by the desktop from these rows. */
    data class Databases(
        override val at: Duration,
        val databases: List<Database>,
    ) : MockEvent {
        data class Database(val id: String, val name: String, val tables: List<Table>)

        data class Table(val name: String, val columns: List<Column>, val rows: List<List<String?>>)

        data class Column(val name: String, val type: String, val primaryKey: Boolean = false, val nullable: Boolean = true)
    }

    /** A query executed by the app, as the SDK logs it. */
    data class DatabaseQueryLog(
        override val at: Duration,
        val databaseId: String,
        val sql: String,
        val bindArgs: List<String> = emptyList(),
        val transactionId: String? = null,
    ) : MockEvent

    /** The root folders of the app and their content: the mock answers the file requests of the desktop from this tree. */
    data class Files(
        override val at: Duration,
        val roots: List<Root>,
    ) : MockEvent {
        data class Root(val id: String, val name: String, val path: String, val children: List<Entry>)

        sealed interface Entry {
            val name: String
            val modifiedAgo: Duration

            data class Folder(override val name: String, override val modifiedAgo: Duration, val children: List<Entry>) : Entry

            data class File(override val name: String, override val modifiedAgo: Duration, val size: Long, val content: String? = null) : Entry
        }
    }

    /** The preference files of the app (SharedPreferences, DataStore...): the mock answers the read / edit requests of the desktop. */
    data class SharedPreferences(
        override val at: Duration,
        val preferences: List<Preference>,
    ) : MockEvent {
        data class Preference(val name: String, val type: String?, val rows: List<Row>)

        data class Row(val key: String, val value: Value)

        sealed interface Value {
            data class Str(val value: String) : Value

            data class Integer(val value: Int) : Value

            data class Long(val value: kotlin.Long) : Value

            data class Float(val value: kotlin.Float) : Value

            data class Bool(val value: Boolean) : Value

            data class StringSet(val value: List<String>) : Value
        }
    }

    /** A dashboard the app displays. */
    data class Dashboard(
        override val at: Duration,
        val dashboardId: String,
        val containers: List<Container>,
    ) : MockEvent {
        data class Container(
            val name: String,
            val elements: List<Element>,
            val formId: String? = null, // null = a section
            val submitText: String? = null,
        )

        sealed interface Element {
            data class Button(val id: String, val text: String) : Element

            data class Label(val label: String, val color: Int? = null) : Element

            data class Text(val label: String, val value: String, val color: Int? = null) : Element

            data class TextField(val id: String, val label: String, val value: String, val placeHolder: String? = null) : Element

            data class CheckBox(val id: String, val label: String, val value: Boolean) : Element

            data class PlainText(val label: String, val value: String, val json: Boolean = false) : Element

            data class Markdown(val label: String, val value: String) : Element

            data class Html(val label: String, val value: String) : Element
        }
    }

    /** One row of a table of the Tables plugin. */
    data class TableRow(
        override val at: Duration,
        val tableName: String,
        val cells: List<Pair<String, String>>,
    ) : MockEvent

    /** What the screen reader of the device is doing: on, off, with which services. */
    data class AccessibilityState(
        override val at: Duration,
        val supported: Boolean,
        val enabled: Boolean,
        val touchExploration: Boolean,
        val services: List<String>,
        val canToggleTalkBack: Boolean,
    ) : MockEvent

    data class AccessibilityEvent(
        override val at: Duration,
        val type: String,
        val text: String?,
        val contentDescription: String?,
        val className: String?,
        val screen: String?,
        val bounds: String?,
        val extras: Map<String, String>,
    ) : MockEvent

    data class WebViewConsole(
        override val at: Duration,
        val webViewId: String,
        val level: String,
        val message: String,
        val url: String?,
        val sourceId: String?,
        val lineNumber: Int?,
    ) : MockEvent

    data class WebViewPage(
        override val at: Duration,
        val webViewId: String,
        val type: String,
        val url: String?,
        val message: String?,
        val statusCode: Int?,
    ) : MockEvent

    data class WebViewBridge(
        override val at: Duration,
        val webViewId: String,
        val url: String?,
        val interfaceName: String,
        val method: String,
        val arguments: String?,
        val result: String?,
    ) : MockEvent

    /** One event of a WebSocket: [event] is `open`, `sent`, `received`, `closing`, `closed` or `error`. */
    data class WebSocket(
        override val at: Duration,
        val url: String,
        val event: String,
        val message: String? = null,
        val error: String? = null,
    ) : MockEvent

    /** A message sent as is, to test what a real SDK should not send (invalid body…). */
    data class Raw(
        override val at: Duration,
        val plugin: String,
        val method: String,
        val body: String,
    ) : MockEvent
}
