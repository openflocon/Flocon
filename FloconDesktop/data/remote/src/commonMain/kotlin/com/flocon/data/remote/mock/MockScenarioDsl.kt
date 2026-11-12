package com.flocon.data.remote.mock

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@DslMarker
annotation class MockScenarioDslMarker

/** Readability only: `5.minutes.ago` is the time before the launch. */
val Duration.ago: Duration get() = this

fun scenario(
    name: String,
    description: String = "",
    block: MockScenarioBuilder.() -> Unit,
): MockScenario = MockScenario(
    name = name,
    description = description,
    devices = MockScenarioBuilder().apply(block).devices.toList(),
)

@MockScenarioDslMarker
class MockScenarioBuilder internal constructor() {
    internal val devices = mutableListOf<MockDevice>()

    fun android(
        name: String,
        app: String,
        packageName: String,
        id: String = name.toDeviceId(),
        appStartedAt: Duration? = null,
        sdkVersion: String? = null,
        block: MockDeviceBuilder.() -> Unit,
    ) = device("android", name, app, packageName, id, appStartedAt, sdkVersion, block)

    fun ios(
        name: String,
        app: String,
        packageName: String,
        id: String = name.toDeviceId(),
        appStartedAt: Duration? = null,
        sdkVersion: String? = null,
        block: MockDeviceBuilder.() -> Unit,
    ) = device("ios", name, app, packageName, id, appStartedAt, sdkVersion, block)

    private fun device(
        platform: String,
        name: String,
        app: String,
        packageName: String,
        id: String,
        appStartedAt: Duration?,
        sdkVersion: String?,
        block: MockDeviceBuilder.() -> Unit,
    ) {
        devices += MockDevice(
            id = id,
            name = name,
            platform = platform,
            appName = app,
            packageName = packageName,
            appStartedAt = appStartedAt,
            sdkVersion = sdkVersion,
            events = MockDeviceBuilder().apply(block).events.toList(),
        )
    }
}

@MockScenarioDslMarker
class MockDeviceBuilder internal constructor() {
    internal val events = mutableListOf<MockEvent>()

    fun network(
        at: Duration,
        method: String,
        url: String,
        status: Int = 200,
        duration: Duration = 100.milliseconds,
        error: String? = null,
        pending: Boolean = false,
        block: MockNetworkCallBuilder.() -> Unit = {},
    ) {
        val details = MockNetworkCallBuilder().apply(block)
        events += MockEvent.NetworkCall(
            at = at,
            method = method,
            url = url,
            status = status,
            duration = duration,
            requestHeaders = details.requestHeaders.toMap(),
            requestBody = details.requestBody,
            responseHeaders = details.responseHeaders.toMap(),
            responseBody = details.responseBody,
            responseSize = details.responseSize,
            contentType = details.contentType,
            error = error,
            pending = pending,
        )
    }

    /** A gRPC call: like the SDK sends it, the url is the server authority and the method the full method name. */
    fun grpc(
        at: Duration,
        authority: String,
        method: String,
        grpcStatus: String = "OK",
        duration: Duration = 100.milliseconds,
        pending: Boolean = false,
    ) {
        events += MockEvent.NetworkCall(
            at = at,
            method = method,
            url = authority,
            duration = duration,
            pending = pending,
            grpcStatus = grpcStatus,
        )
    }

    fun timeline(
        at: Duration,
        value: String,
        type: String = "log",
        level: String? = "info",
        params: Map<String, String> = emptyMap(),
    ) {
        events += MockEvent.TimelineLog(at = at, value = value, type = type, level = level, params = params)
    }

    fun crash(
        at: Duration,
        exceptionType: String,
        message: String,
        stackTrace: String = "",
    ) {
        events += MockEvent.Crash(at = at, exceptionType = exceptionType, message = message, stackTrace = stackTrace)
    }

    fun analytics(
        at: Duration,
        event: String,
        vararg properties: Pair<String, String>,
        tableId: String = "analytics",
    ) {
        events += MockEvent.AnalyticsEvent(at = at, eventName = event, tableId = tableId, properties = properties.toMap())
    }

    fun featureFlags(
        provider: String,
        at: Duration = Duration.ZERO,
        block: MockFeatureFlagsBuilder.() -> Unit,
    ) {
        events += MockEvent.FeatureFlags(at = at, provider = provider, flags = MockFeatureFlagsBuilder().apply(block).flags.toList())
    }

    fun deeplinks(
        at: Duration = Duration.ZERO,
        block: MockDeeplinksBuilder.() -> Unit,
    ) {
        val builder = MockDeeplinksBuilder().apply(block)
        events += MockEvent.Deeplinks(at = at, deeplinks = builder.deeplinks.toList(), variables = builder.variables.toList())
    }

    /**
     * The databases of the app: the desktop lists them, reads their tables and runs its queries on these rows
     * (`SELECT ... FROM <table>` gives the table, any other SQL an error).
     */
    fun databases(
        at: Duration = Duration.ZERO,
        block: MockDatabasesBuilder.() -> Unit,
    ) {
        events += MockEvent.Databases(at = at, databases = MockDatabasesBuilder().apply(block).databases.toList())
    }

    /** A query executed by the app, shown in the query logs of the database screen. */
    fun databaseQuery(
        at: Duration,
        databaseId: String,
        sql: String,
        vararg bindArgs: String,
        transactionId: String? = null,
    ) {
        events += MockEvent.DatabaseQueryLog(at = at, databaseId = databaseId, sql = sql, bindArgs = bindArgs.toList(), transactionId = transactionId)
    }

    /** The root folders of the app: the desktop browses, downloads and deletes in this tree. */
    fun files(
        at: Duration = Duration.ZERO,
        block: MockFilesBuilder.() -> Unit,
    ) {
        events += MockEvent.Files(at = at, roots = MockFilesBuilder().apply(block).roots.toList())
    }

    /** The preference files of the app: the desktop reads, edits and removes their keys. */
    fun sharedPreferences(
        at: Duration = Duration.ZERO,
        block: MockSharedPreferencesBuilder.() -> Unit,
    ) {
        events += MockEvent.SharedPreferences(at = at, preferences = MockSharedPreferencesBuilder().apply(block).preferences.toList())
    }

    /** A dashboard (the same id replaces the previous one). Clicks and edits made on the desktop are ignored. */
    fun dashboard(
        dashboardId: String,
        at: Duration = Duration.ZERO,
        block: MockDashboardBuilder.() -> Unit,
    ) {
        events += MockEvent.Dashboard(at = at, dashboardId = dashboardId, containers = MockDashboardBuilder().apply(block).containers.toList())
    }

    /** A row of the table [tableName] (the tables of the Tables plugin are created by their first row). */
    fun tableRow(
        at: Duration,
        tableName: String,
        vararg cells: Pair<String, String>,
    ) {
        events += MockEvent.TableRow(at = at, tableName = tableName, cells = cells.toList())
    }

    fun accessibilityState(
        enabled: Boolean = true,
        touchExploration: Boolean = true,
        services: List<String> = listOf("com.google.android.marvin.talkback/com.google.android.marvin.talkback.TalkBackService"),
        canToggleTalkBack: Boolean = true,
        supported: Boolean = true,
        at: Duration = Duration.ZERO,
    ) {
        events += MockEvent.AccessibilityState(
            at = at,
            supported = supported,
            enabled = enabled,
            touchExploration = touchExploration,
            services = services,
            canToggleTalkBack = canToggleTalkBack,
        )
    }

    fun accessibility(
        at: Duration,
        type: String,
        text: String? = null,
        contentDescription: String? = null,
        className: String? = null,
        screen: String? = null,
        bounds: String? = null,
        extras: Map<String, String> = emptyMap(),
    ) {
        events += MockEvent.AccessibilityEvent(
            at = at,
            type = type,
            text = text,
            contentDescription = contentDescription,
            className = className,
            screen = screen,
            bounds = bounds,
            extras = extras,
        )
    }

    fun webViewConsole(
        at: Duration,
        webViewId: String,
        level: String,
        message: String,
        url: String? = null,
        sourceId: String? = null,
        lineNumber: Int? = null,
    ) {
        events += MockEvent.WebViewConsole(at, webViewId, level, message, url, sourceId, lineNumber)
    }

    /** [type]: `page_started`, `page_finished`, `navigation`, `error`, `http_error` or `ssl_error`. */
    fun webViewPage(
        at: Duration,
        webViewId: String,
        type: String,
        url: String? = null,
        message: String? = null,
        statusCode: Int? = null,
    ) {
        events += MockEvent.WebViewPage(at, webViewId, type, url, message, statusCode)
    }

    /** A call from the page to a native object exposed to JavaScript. */
    fun webViewBridge(
        at: Duration,
        webViewId: String,
        interfaceName: String,
        method: String,
        arguments: String? = null,
        result: String? = null,
        url: String? = null,
    ) {
        events += MockEvent.WebViewBridge(at, webViewId, url, interfaceName, method, arguments, result)
    }

    fun raw(
        at: Duration,
        plugin: String,
        method: String,
        body: String,
    ) {
        events += MockEvent.Raw(at = at, plugin = plugin, method = method, body = body)
    }
}

@MockScenarioDslMarker
class MockNetworkCallBuilder internal constructor() {
    internal val requestHeaders = linkedMapOf<String, String>()
    internal val responseHeaders = linkedMapOf<String, String>()
    internal var requestBody: String? = null
    internal var responseBody: String? = null
    internal var responseSize: Long? = null
    internal var contentType: String? = null

    fun requestHeader(name: String, value: String) {
        requestHeaders[name] = value
    }

    fun responseHeader(name: String, value: String) {
        responseHeaders[name] = value
    }

    fun requestBody(body: String) {
        requestBody = body
    }

    fun responseBody(body: String) {
        responseBody = body
    }

    /** The size of a response whose body is not kept (an image), in bytes. */
    fun responseSize(bytes: Long) {
        responseSize = bytes
    }

    fun contentType(contentType: String) {
        this.contentType = contentType
    }
}

@MockScenarioDslMarker
class MockFeatureFlagsBuilder internal constructor() {
    internal val flags = mutableListOf<MockEvent.FeatureFlags.Flag>()

    fun flag(
        name: String,
        state: String,
        variant: String,
        payload: String? = null,
    ) {
        flags += MockEvent.FeatureFlags.Flag(name = name, state = state, variant = variant, payload = payload)
    }
}

@MockScenarioDslMarker
class MockDeeplinksBuilder internal constructor() {
    internal val deeplinks = mutableListOf<MockEvent.Deeplinks.Deeplink>()
    internal val variables = mutableListOf<MockEvent.Deeplinks.Variable>()

    fun deeplink(
        link: String,
        label: String? = null,
        description: String? = null,
        block: MockDeeplinkParametersBuilder.() -> Unit = {},
    ) {
        deeplinks += MockEvent.Deeplinks.Deeplink(
            link = link,
            label = label,
            description = description,
            parameters = MockDeeplinkParametersBuilder().apply(block).parameters.toList(),
        )
    }

    /** A variable shared by deeplinks: free input, or a list of suggestions when [suggestions] is set. */
    fun variable(name: String, description: String? = null, suggestions: List<String>? = null) {
        variables += MockEvent.Deeplinks.Variable(name = name, description = description, suggestions = suggestions)
    }
}

@MockScenarioDslMarker
class MockDeeplinkParametersBuilder internal constructor() {
    internal val parameters = mutableListOf<MockEvent.Deeplinks.Parameter>()

    fun autoComplete(name: String, vararg values: String) {
        parameters += MockEvent.Deeplinks.Parameter.AutoComplete(name = name, values = values.toList())
    }

    fun variable(name: String, variableName: String) {
        parameters += MockEvent.Deeplinks.Parameter.Variable(name = name, variableName = variableName)
    }
}

@MockScenarioDslMarker
class MockDatabasesBuilder internal constructor() {
    internal val databases = mutableListOf<MockEvent.Databases.Database>()

    fun database(name: String, id: String = name, block: MockTablesBuilder.() -> Unit = {}) {
        databases += MockEvent.Databases.Database(id = id, name = name, tables = MockTablesBuilder().apply(block).tables.toList())
    }
}

@MockScenarioDslMarker
class MockTablesBuilder internal constructor() {
    internal val tables = mutableListOf<MockEvent.Databases.Table>()

    fun table(name: String, block: MockTableBuilder.() -> Unit) {
        val builder = MockTableBuilder().apply(block)
        tables += MockEvent.Databases.Table(name = name, columns = builder.columns.toList(), rows = builder.rows.toList())
    }
}

@MockScenarioDslMarker
class MockTableBuilder internal constructor() {
    internal val columns = mutableListOf<MockEvent.Databases.Column>()
    internal val rows = mutableListOf<List<String?>>()

    /** A column, [type] is the SQLite type (`INTEGER`, `TEXT`, `REAL`...). */
    fun column(name: String, type: String, primaryKey: Boolean = false, nullable: Boolean = !primaryKey) {
        columns += MockEvent.Databases.Column(name = name, type = type, primaryKey = primaryKey, nullable = nullable)
    }

    /** A row, values in the order of the columns (null = SQL NULL). */
    fun row(vararg values: Any?) {
        rows += values.map { it?.toString() }
    }
}

@MockScenarioDslMarker
class MockFilesBuilder internal constructor() {
    internal val roots = mutableListOf<MockEvent.Files.Root>()

    /** A root folder: [id] is `files`, `caches`, `databases`... for the default ones, [path] is its absolute path on the device. */
    fun root(id: String, name: String, path: String, block: MockFolderBuilder.() -> Unit) {
        roots += MockEvent.Files.Root(id = id, name = name, path = path, children = MockFolderBuilder().apply(block).children.toList())
    }
}

@MockScenarioDslMarker
class MockFolderBuilder internal constructor() {
    internal val children = mutableListOf<MockEvent.Files.Entry>()

    /** A file; [content] is what the desktop downloads (a placeholder when null). */
    fun file(name: String, size: Long, modifiedAgo: Duration, content: String? = null) {
        children += MockEvent.Files.Entry.File(name = name, modifiedAgo = modifiedAgo, size = size, content = content)
    }

    fun folder(name: String, modifiedAgo: Duration, block: MockFolderBuilder.() -> Unit = {}) {
        children += MockEvent.Files.Entry.Folder(name = name, modifiedAgo = modifiedAgo, children = MockFolderBuilder().apply(block).children.toList())
    }
}

@MockScenarioDslMarker
class MockSharedPreferencesBuilder internal constructor() {
    internal val preferences = mutableListOf<MockEvent.SharedPreferences.Preference>()

    /** A preference file, [type]: `SharedPreferences`, `DataStore`, `UserDefaults`, `Keychain`... */
    fun preferences(name: String, type: String? = "SharedPreferences", block: MockPreferenceRowsBuilder.() -> Unit) {
        preferences += MockEvent.SharedPreferences.Preference(name = name, type = type, rows = MockPreferenceRowsBuilder().apply(block).rows.toList())
    }
}

@MockScenarioDslMarker
class MockPreferenceRowsBuilder internal constructor() {
    internal val rows = mutableListOf<MockEvent.SharedPreferences.Row>()

    fun string(key: String, value: String) = add(key, MockEvent.SharedPreferences.Value.Str(value))

    fun int(key: String, value: Int) = add(key, MockEvent.SharedPreferences.Value.Integer(value))

    fun long(key: String, value: Long) = add(key, MockEvent.SharedPreferences.Value.Long(value))

    fun float(key: String, value: Float) = add(key, MockEvent.SharedPreferences.Value.Float(value))

    fun boolean(key: String, value: Boolean) = add(key, MockEvent.SharedPreferences.Value.Bool(value))

    fun stringSet(key: String, vararg values: String) = add(key, MockEvent.SharedPreferences.Value.StringSet(values.toList()))

    private fun add(key: String, value: MockEvent.SharedPreferences.Value) {
        rows += MockEvent.SharedPreferences.Row(key = key, value = value)
    }
}

@MockScenarioDslMarker
class MockDashboardBuilder internal constructor() {
    internal val containers = mutableListOf<MockEvent.Dashboard.Container>()

    fun section(name: String, block: MockDashboardElementsBuilder.() -> Unit) {
        containers += MockEvent.Dashboard.Container(name = name, elements = MockDashboardElementsBuilder().apply(block).elements.toList())
    }

    /** A form: the desktop collects its text fields and check boxes and sends them on submit. */
    fun form(name: String, formId: String, submitText: String = "Submit", block: MockDashboardElementsBuilder.() -> Unit) {
        containers += MockEvent.Dashboard.Container(
            name = name,
            elements = MockDashboardElementsBuilder().apply(block).elements.toList(),
            formId = formId,
            submitText = submitText,
        )
    }
}

@MockScenarioDslMarker
class MockDashboardElementsBuilder internal constructor() {
    internal val elements = mutableListOf<MockEvent.Dashboard.Element>()

    fun button(id: String, text: String) {
        elements += MockEvent.Dashboard.Element.Button(id, text)
    }

    /** [color] is an ARGB integer (`0xFF2E7D32.toInt()`). */
    fun label(label: String, color: Int? = null) {
        elements += MockEvent.Dashboard.Element.Label(label, color)
    }

    fun text(label: String, value: String, color: Int? = null) {
        elements += MockEvent.Dashboard.Element.Text(label, value, color)
    }

    fun textField(id: String, label: String, value: String = "", placeHolder: String? = null) {
        elements += MockEvent.Dashboard.Element.TextField(id, label, value, placeHolder)
    }

    fun checkBox(id: String, label: String, value: Boolean) {
        elements += MockEvent.Dashboard.Element.CheckBox(id, label, value)
    }

    fun plainText(label: String, value: String) {
        elements += MockEvent.Dashboard.Element.PlainText(label, value, json = false)
    }

    fun json(label: String, value: String) {
        elements += MockEvent.Dashboard.Element.PlainText(label, value, json = true)
    }

    fun markdown(label: String, value: String) {
        elements += MockEvent.Dashboard.Element.Markdown(label, value)
    }

    fun html(label: String, value: String) {
        elements += MockEvent.Dashboard.Element.Html(label, value)
    }
}

private fun String.toDeviceId() = lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')
