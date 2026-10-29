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
| Device (app icon, restart app) | `flocon-device` | Android (app icon also on iOS, restart on Android only) |
| Crash reporter | `flocon-crashreporter` | Android, JVM, iOS, Wasm |

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
| `Flocon.initialize(context)` | `startFlocon(context) { install(...) }` (Android) / `startFlocon(FloconContext(...)) { install(...) }` |
| `Flocon.deeplinks { ... }` | `install(FloconDeeplinks) { ... }` |
| `floconAnalytics("stream").logEvents(event)` | `Flocon.analyticsPlugin.log(AnalyticsEvent("name", "stream", ...))` |
| `floconTable("name").log("key" toParam "value")` | `Flocon.tablePlugin.table("name") { column("key", "value") }` |
| `floconDashboard(id) { ... }` | `Flocon.dashboardPlugin.dashboard(id, scope) { ... }` |
| `floconRegisterPreference(preference)` | `Flocon.preferencesPlugin.register(preference)` |
| `floconRegisterDatabase(...)` | `Flocon.databasePlugin.register("name", roomDatabase)` |
| `floconLogDatabaseQuery(...)` | `Flocon.databasePlugin.logQuery(...)` or `RoomDatabase.Builder.floconLogs()` |
| `floconLogWebSocketEvent(event)` | `Flocon.networkPlugin.logWebSocket(event)` |

The accessors are: `analyticsPlugin`, `crashReporterPlugin`, `dashboardPlugin`, `databasePlugin`,
`deeplinksPlugin`, `devicePlugin`, `featureFlagsPlugin`, `filesPlugin`, `networkPlugin`, `preferencesPlugin` and
`tablePlugin` (plus `databaseRoom` / `databaseRoom3` for the Room providers).

---

## 4. Behavior changes

- **Crash reporter**: crashes are captured as soon as `FloconCrashReporter` is installed
  (`catchFatalErrors = true` by default) and sent to the desktop app on the next connection.
- **Interceptors without Flocon**: the OkHttp, Ktor and gRPC interceptors let the calls go through
  untouched when `FloconNetwork` is not installed.
- **Desktop menu**: when it connects, the SDK tells the desktop app which plugins are installed. The desktop only shows the menu items of the installed plugins (an older SDK that does not send them shows everything). Sections can be collapsed, and items can be pinned (pin icon on hover) to stay at the top.
- **Encrypted connection**: the SDK connects to the desktop with `wss://` / `https://` instead of `ws://` / `http://`. The desktop no longer serves the plain ports, so **update the SDK and Flocon Desktop together** (an old SDK cannot connect to a new desktop, and the other way around). The desktop certificate is self-signed and is not verified by the SDK: the connection is encrypted, not authenticated. The cleartext rule for `localhost` / `127.0.0.1` is no longer needed, you can remove it from your `network_security_config.xml`. The `flocon-connect` and `flocon-connect-no-op` modules are gone: `Flocon.connect`, `connect { }` and `Flocon.connectionState` are now part of `flocon` (and `flocon-no-op`), remove the two dependencies.
- **Release builds** now use `flocon-no-op` + one no-op per plugin: nothing is started and no
  connection is made to the desktop app.
