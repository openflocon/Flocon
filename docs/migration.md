---
title: Migration
description: Breaking changes of the Flocon Kotlin SDK and how to migrate: one flocon-no-op for release builds (after 2.0.0), and 1.x to 2.0, plus a note for the Swift and Flutter SDKs.
---

# 🧭 Migration

## After 2.0.0: one `flocon-no-op` for release builds

This change is for the **Kotlin** SDK only. The per-module `-no-op` artifacts are no longer published: the real modules now go in every variant, and a single `flocon-no-op` in release turns Flocon off. See [Release builds](install-kotlin.md#release-builds).

**Removed artifacts** (remove them from your build files and version catalog):

`flocon-network-core-no-op`, `flocon-okhttp-interceptor-no-op`, `flocon-ktor-interceptor-no-op`,
`flocon-grpc-interceptor-no-op`, `flocon-grpc-interceptor-lite-no-op`, `flocon-grpc-interceptor-base-no-op`,
`flocon-database-core-no-op`, `flocon-database-room-no-op`, `flocon-database-room3-no-op`, `flocon-database-sqldelight-no-op`,
`flocon-analytics-no-op`, `flocon-crashreporter-no-op`, `flocon-datastores-no-op`, `flocon-deeplinks-no-op`,
`flocon-feature-flags-no-op`, `flocon-files-no-op`, `flocon-sharedprefs-no-op`, `flocon-tables-no-op`,
`flocon-timeline-no-op`, `flocon-webview-no-op`.

`flocon-no-op`, `flocon-accessibility-no-op` and `flocon-qr-scanner-no-op` are still published.

**Modules merged into `flocon`**: `flocon-analytics`, `flocon-files` and `flocon-sharedprefs` have no external dependency, so they are now part of the core module and no longer published as separate artifacts. Remove them from your build files and version catalog: `flocon` is enough, and the packages and APIs are the same (`FloconAnalytics`, `FloconFiles`, `FloconPreferences`, `Flocon.analyticsPlugin`...). `flocon-datastores` is unchanged.

=== "After 2.0.0"

    ```kotlin
    dependencies {
        implementation(libs.flocon)
        implementation(libs.flocon.network)
        implementation(libs.flocon.okhttp)

        // turns Flocon off in release builds
        releaseImplementation(libs.flocon.no.op)
    }
    ```

=== "2.0.0"

    ```kotlin
    dependencies {
        debugImplementation(libs.flocon)
        releaseImplementation(libs.flocon.no.op)

        debugImplementation(libs.flocon.network)
        releaseImplementation(libs.flocon.network.no.op)
        debugImplementation(libs.flocon.okhttp)
        releaseImplementation(libs.flocon.okhttp.no.op)
    }
    ```

- **`debugImplementation` becomes `implementation`** for every Flocon module, the module `-no-op` lines are removed, and so are `flocon-analytics`, `flocon-files` and `flocon-sharedprefs` (now in `flocon`). Keep only `releaseImplementation` of `flocon-no-op` (add it to any other build type that ships).
- **Accessibility and QR code scanner** keep their no-op, because their Android manifest entries can't be removed by `flocon-no-op`: keep `debugImplementation` of the real module and `releaseImplementation` of `flocon-accessibility-no-op` / `flocon-qr-scanner-no-op`.
- **Kotlin Multiplatform**: remove the dependency substitution of every module, except `flocon-accessibility` and `flocon-qr-scanner` if you use them, and add `releaseImplementation` of `flocon-no-op` in the Android app.
- **Turn R8 on in release** to remove the Flocon code from the app: without minification, Flocon is off at runtime but its code and dependencies stay in the binary.
- The BOM still aligns every Flocon module, `flocon-no-op` included.

---

## From 1.x to 2.0

!!! info "Swift and Flutter SDKs"
    This guide is for the **Kotlin** SDK, the only one that had a 1.x. The Swift SDK ([FloconSwift](https://github.com/openflocon/FloconSwift)) and the Flutter SDK ([FloconFlutter](https://github.com/openflocon/FloconFlutter), packages `flocon`, `flocon_dio`...) were published as 0.x pre-releases and are aligned on **2.0.0** with the desktop and the Kotlin SDK. There is nothing to migrate if you start with them, see [Getting started](setup.md). If you used a 0.x version, update to 2.0.0 (the Flutter changelog lists no user-facing change for 2.0.0; the Swift SDK has no changelog) and keep the SDK and the desktop on the same major version.

Flocon 2.0 splits the SDK into **one module per feature**: you only add what you use, and
`flocon-no-op` turns Flocon off in release builds (2.0.0 had one `-no-op` per module, see [above](#after-200-one-flocon-no-op-for-release-builds)). The SDK is now initialized with a small DSL where you
`install` the plugins you need, and every plugin is reached the same way: `Flocon.xxxPlugin`.

---

### 1. Dependencies

In 1.x, `io.github.openflocon:flocon` contained every feature. In 2.0, `flocon` is the core
(connection to the desktop app + dashboards) and each feature has its own artifact:

| Feature | Artifact | Platforms |
| :--- | :--- | :--- |
| Core + Dashboards | `flocon` | Android, JVM, iOS, Wasm |
| Network (mocks, bad network) | `flocon-network-core` | Android, JVM, iOS, Wasm |
| OkHttp interceptor + WebSockets | `flocon-okhttp-interceptor` | Android, JVM |
| Ktor plugin | `flocon-ktor-interceptor` | Android, JVM, iOS, Wasm |
| gRPC interceptor | `flocon-grpc-interceptor` / `flocon-grpc-interceptor-lite` (+ `flocon-grpc-interceptor-base`) | Android |
| Databases | `flocon-database-core` + `flocon-database-room` (Room 2), `flocon-database-room3` (Room 3) or `flocon-database-sqldelight` | Android, JVM, iOS (+ Wasm for Room 3) |
| SharedPreferences / custom stores | `flocon-sharedprefs` | Android, JVM, iOS, Wasm |
| DataStore | `flocon-datastores` | Android, JVM, iOS |
| Analytics | `flocon-analytics` | Android, JVM, iOS, Wasm |
| Feature flags | `flocon-feature-flags` | Android, JVM, iOS, Wasm |
| Tables | `flocon-tables` | Android, JVM, iOS, Wasm |
| Deeplinks | `flocon-deeplinks` | Android, JVM, iOS, Wasm |
| Files | `flocon-files` | Android, JVM, iOS (Wasm: no file browsing) |
| Crash reporter | `flocon-crashreporter` | Android, JVM, iOS, Wasm |
| Timeline | `flocon-timeline` | Android, JVM, iOS, Wasm |
| Accessibility | `flocon-accessibility` | Android, JVM, iOS, Wasm |
| WebView | `flocon-webview` | Android, JVM, iOS, Wasm |
| QR code scanner | `flocon-qr-scanner` | Android |

!!! warning "`flocon-base` is gone"
    The 1.x `flocon-base` artifact no longer exists. Code that logged WebSocket events or database queries
    from a module compiled in every variant must now depend on `flocon-network-core` / `flocon-database-core`.

Add the modules with `implementation`, and `flocon-no-op` with `releaseImplementation`: it turns Flocon off in release. Only `flocon-accessibility` and `flocon-qr-scanner` keep a `-no-op` artifact. See [Release builds](install-kotlin.md#release-builds).

#### Platform support details

- **Wasm**: connects to the desktop app (WebSocket) but has no filesystem or SQLite, so files, databases and crash persistence are not available.
- **iOS**: files, crash reporter (stored in Application Support), network mock persistence, `UserDefaults` preferences and the app icon work. Databases are searched in Documents/Library, or in the folders given with `path(...)`.
- **JVM**: Room databases are found in the folders given with `room { path("...") }`.

---

### 2. Initialization

=== "2.0"

    ```kotlin
    startFlocon(FloconContext(applicationContext)) {
        install(FloconNetwork)
        install(FloconDatabase) {
            room()
        }
        install(FloconAnalytics)
        install(FloconDeeplinks) {
            deeplink("myapp://home")
        }
    }

    // later, e.g. once the user is logged in
    Flocon.deeplinksPlugin.register {
        deeplink("myapp://user/[userId]")
    }
    ```

=== "1.x"

    ```kotlin
    Flocon.initialize(this)

    Flocon.deeplinks {
        deeplink("myapp://home")
    }
    ```

`FloconContext` is platform specific: `FloconContext(context)` on Android,
`FloconContext(appName, packageName)` on JVM, `FloconContext()` on iOS and Wasm.

A feature only works if its plugin is installed. Calling a plugin that is not installed never
crashes: Flocon logs it (with `FloconLogger.enabled = true`) and the call does nothing.

---

### 3. API mapping

Every plugin is now accessed with `Flocon.xxxPlugin`:

| 1.x | 2.0 |
| :--- | :--- |
| `Flocon.initialize(context)` | `startFlocon(FloconContext(context)) { install(...) }` |
| `Flocon.deeplinks { ... }` | `install(FloconDeeplinks) { ... }` |
| `floconAnalytics("stream").logEvents(event)` | `Flocon.analyticsPlugin.log(AnalyticsEvent("name", "stream", ...))` |
| `floconTable("name").log("key" toParam "value")` | `Flocon.tablePlugin.table("name") { column("key", "value") }` |
| `floconDashboard(id) { ... }` | `Flocon.dashboardPlugin.dashboard(id, scope) { ... }` |
| `floconRegisterPreference(preference)` | `Flocon.preferencesPlugin.register(preference)` |
| `floconRegisterDatabase(...)` | `Flocon.databasePlugin.register("id", "name", roomDatabase)` (the id is required and unique) |
| `floconLogDatabaseQuery(...)` | `Flocon.databasePlugin.logQuery(dbId, ...)` or `RoomDatabase.Builder.floconLogs(id)`, with the id the database is registered with |
| `floconLogWebSocketEvent(event)` | `Flocon.networkPlugin.logWebSocket(event)` |
| `Flocon.deeplinks { ... }` (registered later) | `Flocon.deeplinksPlugin.register { ... }` |
| `Flocon.analytics("table").logEvents(event)` | `Flocon.analyticsPlugin.log(AnalyticsEvent(name, "table", props))` or `.sender("table").log(...)` |

Packages moved with the modules, for example `io.github.openflocon.flocon.plugins.network.model.FloconWebSocketEvent`
became `io.github.openflocon.flocon.network.core.model.FloconWebSocketEvent`, and
`...plugins.analytics.model.*` became `...analytics.model.*`. Fix the imports after changing the dependencies.

The accessors are: `analyticsPlugin`, `crashReporterPlugin`, `dashboardPlugin`, `databasePlugin`,
`deeplinksPlugin`, `featureFlagsPlugin`, `filesPlugin`, `networkPlugin`, `preferencesPlugin` and
`tablePlugin`, `timelinePlugin`, `webViewPlugin` and `accessibilityPlugin` (plus `databaseRoom` / `databaseRoom3` for the Room providers).

---

### 4. Behavior changes

- **Crash reporter**: crashes are captured as soon as `FloconCrashReporter` is installed
  (`catchFatalErrors = true` by default) and sent to the desktop app on the next connection.
- **Interceptors without Flocon**: the OkHttp, Ktor and gRPC interceptors let the calls go through
  untouched when `FloconNetwork` is not installed.
- **Desktop menu**: when it connects, the SDK tells the desktop app which plugins are installed. The desktop only shows the menu items of the installed plugins (an older SDK that does not send them shows everything). Sections can be collapsed, and items can be pinned (pin icon on hover) to stay at the top.
- **Encrypted connection**: the SDK connects to the desktop with `wss://` / `https://` instead of `ws://` / `http://`. The desktop no longer serves the plain ports, so **update the SDK and Flocon Desktop together** (an old SDK cannot connect to a new desktop, and the other way around). The desktop certificate is self-signed and is not verified by the SDK: the connection is encrypted, not authenticated. The cleartext rule for `localhost` / `127.0.0.1` (and the one `flocon-connect` used to add for any IP address) is no longer needed, you can remove it from your `network_security_config.xml`.
- **Preference type**: `FloconPreference` has a new `type` property, shown next to the preference name in the desktop app. It defaults to `"Custom"` in Kotlin; a Swift class implementing the Kotlin `FloconPreference` interface through the KMP framework must declare it (Kotlin default values are not visible from Swift).
- **Release builds** use `flocon-no-op` (2.0.0: plus one no-op per plugin): nothing is started and no
  connection is made to the desktop app.

---

### 5. Pitfalls

- **A plugin must be installed.** `Flocon.dashboardPlugin.dashboard(...)` compiles without `install(FloconDashboard)`
  but does nothing at runtime. After migrating, list every `Flocon.xxxPlugin` you call and check each one is installed.
- **Name clashes.** `FloconDashboard`, `FloconNetwork`... are now objects of the SDK. If your project has a class
  with the same name, import the SDK one with an alias (`import ...FloconDashboard as FloconDashboardPlugin`).
- **Code compiled in every variant.** A call to a plugin from `main` needs the real module with `implementation`,
  and `flocon-no-op` in release (and in any other build type that ships, e.g. an internal distribution build).
- **Databases need a provider.** `install(FloconDatabase)` alone discovers nothing: add the Room provider with
  `install(FloconDatabase) { room() }` (and the `flocon-database-room` or `-room3` dependency). On Android it then scans
  the app databases folder; `Flocon.databasePlugin.register(id, name, db)` is only needed for in-memory databases or a custom name.
  `logQuery` takes the id of the database as the desktop lists it.
- **No Device plugin.** The app icon is sent by the core, nothing to install. The `flocon-device` module and the
  "restart the app" action of the desktop no longer exist: remove `flocon-device` and `install(FloconDevice)` if the project has them.
- **Cleartext rules.** The `localhost` / `127.0.0.1` cleartext exceptions in `network_security_config.xml` are no longer needed.
- **Gradle task names** differ per module (flavors or not). List them with `./gradlew :module:tasks --all` before building.

---

### 6. Migrating with an AI agent

The `flocon-setup` skill of the Flocon plugin follows this guide: it detects the 1.x usage, picks the modules, updates
the dependencies and `flocon-no-op`, rewrites the initialization and compiles. See [MCP](mcp.md) for the plugin install.
