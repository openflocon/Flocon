---
title: Kotlin SDK (Android & KMP)
description: Add the Flocon Kotlin SDK to an Android or Kotlin Multiplatform app, start it, and keep it out of release builds.
---

# Kotlin SDK (Android & KMP)

[![Maven Central](https://img.shields.io/maven-central/v/io.github.openflocon/flocon?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.openflocon/flocon)

Replace `LAST_VERSION` with the version shown on the badge.

Since 2.0 the SDK is split into **one module per feature**: add the core, then only the modules you use. Every module has a `-no-op` twin with the same API, so release builds ship nothing.

Requirements: Android API 23+, or a Kotlin Multiplatform project (Android, JVM, iOS, Wasm).

!!! tip "Let your AI agent do it"
    The [Flocon plugin](install-with-ai.md) adds the dependencies, starts Flocon and keeps it out of release builds for you.

## 1. Add the dependencies

=== "Version catalog"

    ```toml
    [versions]
    flocon = "LAST_VERSION"

    [libraries]
    flocon = { module = "io.github.openflocon:flocon", version.ref = "flocon" }
    flocon-no-op = { module = "io.github.openflocon:flocon-no-op", version.ref = "flocon" }

    flocon-network = { module = "io.github.openflocon:flocon-network-core", version.ref = "flocon" }
    flocon-network-no-op = { module = "io.github.openflocon:flocon-network-core-no-op", version.ref = "flocon" }
    flocon-okhttp = { module = "io.github.openflocon:flocon-okhttp-interceptor", version.ref = "flocon" }
    flocon-okhttp-no-op = { module = "io.github.openflocon:flocon-okhttp-interceptor-no-op", version.ref = "flocon" }
    flocon-analytics = { module = "io.github.openflocon:flocon-analytics", version.ref = "flocon" }
    flocon-analytics-no-op = { module = "io.github.openflocon:flocon-analytics-no-op", version.ref = "flocon" }
    # ... one pair per module you use, see the table below
    ```

    ```kotlin
    // app/build.gradle.kts
    dependencies {
        debugImplementation(libs.flocon)
        releaseImplementation(libs.flocon.no.op)

        debugImplementation(libs.flocon.network)
        releaseImplementation(libs.flocon.network.no.op)
        debugImplementation(libs.flocon.okhttp)
        releaseImplementation(libs.flocon.okhttp.no.op)
        debugImplementation(libs.flocon.analytics)
        releaseImplementation(libs.flocon.analytics.no.op)
    }
    ```

=== "BOM"

    The `flocon-bom` pins every Flocon module (and its `-no-op`) to the same version: declare the version once, then leave the modules without a version.

    ```toml
    [libraries]
    flocon-bom = { module = "io.github.openflocon:flocon-bom", version = "LAST_VERSION" }
    flocon = { module = "io.github.openflocon:flocon" }
    flocon-no-op = { module = "io.github.openflocon:flocon-no-op" }
    flocon-network = { module = "io.github.openflocon:flocon-network-core" }
    flocon-network-no-op = { module = "io.github.openflocon:flocon-network-core-no-op" }
    ```

    ```kotlin
    dependencies {
        debugImplementation(platform(libs.flocon.bom))
        releaseImplementation(platform(libs.flocon.bom))

        debugImplementation(libs.flocon)
        releaseImplementation(libs.flocon.no.op)

        debugImplementation(libs.flocon.network)
        releaseImplementation(libs.flocon.network.no.op)
    }
    ```

=== "Plain Gradle (Android)"

    ```kotlin
    dependencies {
        debugImplementation("io.github.openflocon:flocon:LAST_VERSION")
        debugImplementation("io.github.openflocon:flocon-network-core:LAST_VERSION")
        debugImplementation("io.github.openflocon:flocon-okhttp-interceptor:LAST_VERSION")

        releaseImplementation("io.github.openflocon:flocon-no-op:LAST_VERSION")
        releaseImplementation("io.github.openflocon:flocon-network-core-no-op:LAST_VERSION")
        releaseImplementation("io.github.openflocon:flocon-okhttp-interceptor-no-op:LAST_VERSION")
    }
    ```

=== "Kotlin Multiplatform"

    ```kotlin
    kotlin {
        sourceSets {
            commonMain.dependencies {
                implementation("io.github.openflocon:flocon:LAST_VERSION")
                implementation("io.github.openflocon:flocon-network-core:LAST_VERSION")
                implementation("io.github.openflocon:flocon-ktor-interceptor:LAST_VERSION")
            }
        }
    }
    ```

    A multiplatform module has a single variant: swap the real modules for the no-ops in the release configurations of the app, see [Release builds](#release-builds).

**All modules** (Maven group `io.github.openflocon`; add `-no-op` to the artifact name for the release twin):

| Feature | Artifact | Platforms |
| :--- | :--- | :--- |
| Core, [Dashboards](dashboard.md) | `flocon` | Android, JVM, iOS, Wasm |
| [Network](network.md) (mocks, bad network, WebSocket logging) | `flocon-network-core` | Android, JVM, iOS, Wasm |
| OkHttp interceptor (+ WebSockets) | `flocon-okhttp-interceptor` | Android, JVM |
| Ktor plugin | `flocon-ktor-interceptor` | Android, JVM, iOS, Wasm |
| [gRPC](grpc.md) | `flocon-grpc-interceptor`, `flocon-grpc-interceptor-lite`, `flocon-grpc-interceptor-base` | Android |
| [Databases](database.md) | `flocon-database-core` + `flocon-database-room` (Room 2) or `flocon-database-room3` (Room 3) or `flocon-database-sqldelight` | Android, JVM, iOS (+ Wasm for Room 3) |
| [Preferences](sharedpref.md) | `flocon-sharedprefs` | Android, JVM, iOS, Wasm |
| DataStore | `flocon-datastores` | Android, JVM, iOS |
| [Files](files.md) | `flocon-files` | Android, JVM, iOS, Wasm (no file browsing on Wasm) |
| [Tables](table.md) | `flocon-tables` | Android, JVM, iOS, Wasm |
| [Deeplinks](deeplink.md) | `flocon-deeplinks` | Android, JVM, iOS, Wasm |
| [Analytics](analytics.md) | `flocon-analytics` | Android, JVM, iOS, Wasm |
| [Feature flags](featureflags.md) | `flocon-feature-flags` | Android, JVM, iOS, Wasm |
| [Timeline](timeline.md) | `flocon-timeline` | Android, JVM, iOS, Wasm |
| [Crash reporter](crashreporter.md) | `flocon-crashreporter` | Android, JVM, iOS, Wasm |
| [Accessibility](accessibility.md) | `flocon-accessibility` | Android, JVM, iOS, Wasm (automatic capture on Android and iOS) |
| [WebView](webview.md) | `flocon-webview` | Android, JVM, iOS, Wasm (automatic `attach` on Android) |
| QR code scanner | `flocon-qr-scanner` | Android |
| BOM | `flocon-bom` | all |

## 2. Start Flocon

Start the SDK as early as possible, then `install` the features you use. A feature that is not installed does nothing instead of crashing, so calls to it can stay in your code.

=== "Android"

    ```kotlin
    class MyApp : Application() {
        override fun onCreate() {
            super.onCreate()

            startFlocon(FloconContext(this)) {
                install(FloconNetwork)
                install(FloconAnalytics)
            }
        }
    }
    ```

=== "Desktop (JVM)"

    ```kotlin
    fun main() {
        startFlocon(FloconContext(appName = "My App", packageName = "com.example.myapp")) {
            install(FloconNetwork)
            install(FloconAnalytics)
        }
        // launch your application
    }
    ```

=== "iOS / Wasm (KMP)"

    ```kotlin
    fun initFlocon() {
        startFlocon(FloconContext()) {
            install(FloconNetwork)
            install(FloconAnalytics)
        }
    }
    ```

Each plugin is then reachable with `Flocon.xxxPlugin` (for example `Flocon.analyticsPlugin.log(...)`). Set `FloconLogger.enabled = true` to see what Flocon does (connection, plugins not installed...).

| Plugin to `install` | Module | Page |
| :--- | :--- | :--- |
| `FloconNetwork` | `flocon-network-core` (+ OkHttp / Ktor / gRPC interceptor) | [Network](network.md) |
| `FloconDatabase` | `flocon-database-core` + a Room / SQLDelight provider | [Databases](database.md) |
| `FloconPreferences` | `flocon-sharedprefs` (+ `flocon-datastores`) | [Preferences](sharedpref.md) |
| `FloconFiles` | `flocon-files` | [Files](files.md) |
| `FloconDashboard` | `flocon` | [Dashboards](dashboard.md) |
| `FloconTable` | `flocon-tables` | [Tables](table.md) |
| `FloconDeeplinks` | `flocon-deeplinks` | [Deeplinks](deeplink.md) |
| `FloconAnalytics` | `flocon-analytics` | [Analytics](analytics.md) |
| `FloconFeatureFlags` | `flocon-feature-flags` | [Feature flags](featureflags.md) |
| `FloconCrashReporter` | `flocon-crashreporter` | [Crash reporter](crashreporter.md) |
| `FloconWebView` | `flocon-webview` | [WebView](webview.md) |
| `FloconTimeline` | `flocon-timeline` | [Timeline](timeline.md) |
| `FloconAccessibility` | `flocon-accessibility` | [Accessibility](accessibility.md) |

## Release builds

Flocon is a debugging tool: once connected, the desktop can read your network calls, edit your preferences, delete files and run SQL on your databases. Keep it out of release builds.

Use `debugImplementation` with the `-no-op` twin in `releaseImplementation`, as in [step 1](#1-add-the-dependencies). The no-op has the same API: nothing is started and no connection is made. Use `flocon-no-op` **and** the no-op of every module you use. In a shared Kotlin Multiplatform module, which has a single variant, swap the modules in the release configurations of the app:

```kotlin
// app/build.gradle.kts
val floconNoOps = mapOf(
    "io.github.openflocon:flocon" to "io.github.openflocon:flocon-no-op:LAST_VERSION",
    "io.github.openflocon:flocon-network-core" to "io.github.openflocon:flocon-network-core-no-op:LAST_VERSION",
    "io.github.openflocon:flocon-ktor-interceptor" to "io.github.openflocon:flocon-ktor-interceptor-no-op:LAST_VERSION",
    // ... one line per Flocon module you use
)

configurations.configureEach {
    if (name.startsWith("release")) {
        resolutionStrategy.dependencySubstitution {
            floconNoOps.forEach { (module, noOp) ->
                substitute(module(module)).using(module(noOp))
            }
        }
    }
}
```

The no-ops are binary compatible: code compiled against the real modules runs on them.

## Next: connect

Run the app with Flocon Desktop open: on an Android emulator or a USB device it connects by itself. See [Connect](connect.md).
