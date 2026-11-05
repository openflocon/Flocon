---
title: Migrating to 2.0
description: What changed in Flocon SDK 2.0 and how to migrate an app from 1.x.
---

# 🧭 Migrating from 1.x to 2.0

Flocon 2.0 splits the SDK into **one module per feature**: you only add what you use, and every
module has a `-no-op` twin for release builds. The SDK is now initialized with a small DSL where you
`install` the plugins you need, and every plugin is reached the same way: `Flocon.xxxPlugin`.

---

## 1. Dependencies

In 1.x, `io.github.openflocon:flocon` contained every feature. In 2.0, `flocon` is the core
(connection to the desktop app + dashboards) and each feature has its own artifact:

| Feature | Artifact | Platforms |
| :--- | :--- | :--- |
| Core + Dashboards | `flocon` | Android, JVM, iOS, Wasm |
| Network (mocks, bad network) | `flocon-network-core` | Android, JVM, iOS, Wasm |
| OkHttp interceptor + WebSockets | `flocon-okhttp-interceptor` | Android, JVM |
| Ktor plugin | `flocon-ktor-interceptor` | Android, JVM, iOS, Wasm |
| gRPC interceptor | `flocon-grpc-interceptor` / `flocon-grpc-interceptor-lite` | Android |
| Databases | `flocon-database-core` + `flocon-database-room` (Room 2) or `flocon-database-room3` (Room 3) | Android, JVM, iOS (+ Wasm for Room 3) |
| SharedPreferences / custom stores | `flocon-sharedprefs` | Android, JVM, iOS, Wasm |
| DataStore | `flocon-datastores` | Android, JVM, iOS |
| Analytics | `flocon-analytics` | Android, JVM, iOS, Wasm |
| Feature flags | `flocon-feature-flags` | Android, JVM, iOS, Wasm |
| Tables | `flocon-tables` | Android, JVM, iOS, Wasm |
| Deeplinks | `flocon-deeplinks` | Android, JVM, iOS, Wasm |
| Files | `flocon-files` | Android, JVM, iOS (Wasm: no file browsing) |
| Crash reporter | `flocon-crashreporter` | Android, JVM, iOS, Wasm |

!!! warning "`flocon-base` is gone"
    The 1.x `flocon-base` artifact no longer exists. Code that logged WebSocket events or database queries
    from a module compiled in every variant must now depend on `flocon-network-core` / `flocon-database-core`
    in debug and on their `-no-op` in release.

Every artifact has a **`-no-op`** version with the same API (e.g. `flocon-analytics-no-op`). See [Release builds](setup.md#4-release-builds-no-op).

### Platform support details

- **Wasm**: connects to the desktop app (WebSocket) but has no filesystem or SQLite, so files, databases and crash persistence are not available.
- **iOS**: files, crash reporter (stored in Application Support), network mock persistence, `UserDefaults` preferences and the app icon work. Databases are searched in Documents/Library, or in the folders given with `path(...)`.
- **JVM**: Room databases are found in the folders given with `room { path("...") }`.

---

## 2. Initialization

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

## 3. API mapping

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
`tablePlugin` (plus `databaseRoom` / `databaseRoom3` for the Room providers).

---

## 4. Behavior changes

- **Crash reporter**: crashes are captured as soon as `FloconCrashReporter` is installed
  (`catchFatalErrors = true` by default) and sent to the desktop app on the next connection.
- **Interceptors without Flocon**: the OkHttp, Ktor and gRPC interceptors let the calls go through
  untouched when `FloconNetwork` is not installed.
- **Desktop menu**: when it connects, the SDK tells the desktop app which plugins are installed. The desktop only shows the menu items of the installed plugins (an older SDK that does not send them shows everything). Sections can be collapsed, and items can be pinned (pin icon on hover) to stay at the top.
- **Encrypted connection**: the SDK connects to the desktop with `wss://` / `https://` instead of `ws://` / `http://`. The desktop no longer serves the plain ports, so **update the SDK and Flocon Desktop together** (an old SDK cannot connect to a new desktop, and the other way around). The desktop certificate is self-signed and is not verified by the SDK: the connection is encrypted, not authenticated. The cleartext rule for `localhost` / `127.0.0.1` (and the one `flocon-connect` used to add for any IP address) is no longer needed, you can remove it from your `network_security_config.xml`.
- **Preference type**: `FloconPreference` has a new `type` property, shown next to the preference name in the desktop app. It defaults to `"Custom"` in Kotlin; a Swift class implementing `FloconPreference` must declare it.
- **Release builds** now use `flocon-no-op` + one no-op per plugin: nothing is started and no
  connection is made to the desktop app.

---

## 5. Pitfalls

- **A plugin must be installed.** `Flocon.dashboardPlugin.dashboard(...)` compiles without `install(FloconDashboard)`
  but does nothing at runtime. After migrating, list every `Flocon.xxxPlugin` you call and check each one is installed.
- **Name clashes.** `FloconDashboard`, `FloconNetwork`... are now objects of the SDK. If your project has a class
  with the same name, import the SDK one with an alias (`import ...FloconDashboard as FloconDashboardPlugin`).
- **Code compiled in every variant.** A call to a plugin from `main` needs the real module in debug **and** its
  `-no-op` in release (and in any other non-debug variant, e.g. an internal distribution build).
- **Databases need a provider.** `install(FloconDatabase)` alone discovers nothing: add the Room provider with
  `install(FloconDatabase) { room() }` (and the `flocon-database-room` or `-room3` dependency). On Android it then scans
  the app databases folder; `Flocon.databasePlugin.register(id, name, db)` is only needed for in-memory databases or a custom name.
  `logQuery` takes the id of the database as the desktop lists it.
- **No Device plugin.** The app icon is sent by the core, nothing to install. The `flocon-device` module and the
  "restart the app" action of the desktop no longer exist: remove `flocon-device` and `install(FloconDevice)` if the project has them.
- **Cleartext rules.** The `localhost` / `127.0.0.1` cleartext exceptions in `network_security_config.xml` are no longer needed.
- **Gradle task names** differ per module (flavors or not). List them with `./gradlew :module:tasks --all` before building.

---

## 6. Migrating with an AI agent

The `flocon-setup` skill of the Flocon plugin follows this guide: it detects the 1.x usage, picks the modules, updates
the dependencies and the no-ops, rewrites the initialization and compiles. See [MCP](mcp.md) for the plugin install.
